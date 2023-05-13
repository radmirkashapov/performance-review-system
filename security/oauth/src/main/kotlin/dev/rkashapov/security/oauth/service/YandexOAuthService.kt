package dev.rkashapov.security.oauth.service

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.map.IMap
import dev.rkashapov.base.caching.CollectionName
import dev.rkashapov.base.logging.MdcKey
import dev.rkashapov.base.logging.withLoggingContext
import dev.rkashapov.security.core.service.JWTService
import dev.rkashapov.security.oauth.api.model.OAuthStateModel
import dev.rkashapov.security.oauth.configuration.OAuthConfigurationProperties
import dev.rkashapov.security.oauth.entity.OAuthTokenEntity
import dev.rkashapov.security.oauth.exception.InvalidStateException
import dev.rkashapov.security.oauth.repository.OAuthTokenRepository
import dev.rkashapov.user.entity.UserEntity
import dev.rkashapov.user.repository.UserRepository
import io.jsonwebtoken.security.Keys
import mu.KLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClientException
import ru.ya.oauth2.api.client.YandexOAuth2LoginInfoClient
import ru.ya.oauth2.api.client.YandexOAuth2TokenClient
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class YandexOAuthService(
    private val yandexOAuth2TokenClient: YandexOAuth2TokenClient,
    private val oAuthTokenRepository: OAuthTokenRepository,
    private val yandexOAuth2LoginInfoClient: YandexOAuth2LoginInfoClient,
    private val userRepository: UserRepository,
    private val jwtService: JWTService,
    private val oAuthConfigurationProperties: OAuthConfigurationProperties,
    private val hazelcastInstance: HazelcastInstance
) : KLogging() {

    private val oauthStates: IMap<String, OAuthStateModel> = hazelcastInstance.getMap(CollectionName.oauthStatesMap)
    private val stateJWTSigningKey =
        checkNotNull(Keys.hmacShaKeyFor(oAuthConfigurationProperties.stateKey.toByteArray()))

    @Transactional
    fun getOAuth2Link(deviceName: String, deviceId: String, requestId: String?): String {
        return withLoggingContext(MdcKey.REQUEST_ID to requestId, MdcKey.DEVICE_ID to deviceId) {
            logger.info { "Requested oauth2 link for YANDEX" }

            val now = Instant.now()

            val stateValue = jwtService
                .generateToken(
                    subject = deviceId,
                    issuer = oAuthConfigurationProperties.issuer,
                    issuedAt = Instant.now(),
                    expiresAt = now.plus(5L, ChronoUnit.MINUTES), // short-lived token
                    signingKey = stateJWTSigningKey,
                    requestId = requestId
                )

            oauthStates.putIfAbsent(stateValue, OAuthStateModel(deviceName, deviceId))

            logger.info { "Saved oauth token meta to hazelcast" }

            withLoggingContext() {
                yandexOAuth2TokenClient
                    .buildUrlForUser(
                        state = stateValue,
                        deviceName = deviceName,
                        deviceId = deviceId
                    )
            }
        }
    }

    @Transactional(rollbackFor = [WebClientException::class])
    fun processYandexCodeCallback(stateId: String, code: String, requestId: String): UserEntity {
        withLoggingContext(MdcKey.REQUEST_ID to requestId) {

            logger.info { "Processing yandex code callback" }

            jwtService.validateAndParseToken(
                token = stateId,
                key = stateJWTSigningKey,
                requestId = requestId
            )

            val tokenMeta = getOAuthTokenMeta(stateId)

            logger.trace { "tokenMeta: $tokenMeta" }


            return withLoggingContext(MdcKey.DEVICE_ID to tokenMeta.deviceId) {
                var currentOAuthToken = oAuthTokenRepository.findFirstByDeviceId(tokenMeta.deviceId)
                val now = Instant.now()

                if (currentOAuthToken == null) {

                    logger.info { "OAuth token haven't initialized yet. Processing..." }

                    val token = yandexOAuth2TokenClient
                        .swapCodeToToken(code = code, deviceId = tokenMeta.deviceId, deviceName = tokenMeta.deviceName)
                        .block()

                    requireNotNull(token)

                    currentOAuthToken = OAuthTokenEntity(
                        deviceId = tokenMeta.deviceId,
                        deviceName = tokenMeta.deviceName,
                        accessToken = token.accessToken,
                        refreshToken = token.refreshToken,
                        expiresIn = now.plusSeconds(token.expiresInSeconds).minusSeconds(60L)
                    )
                }

                val user = upsertUser(currentOAuthToken.accessToken)
                currentOAuthToken.owner = user

                oAuthTokenRepository.saveAndFlush(currentOAuthToken)

                oauthStates.delete(stateId)

                currentOAuthToken.owner!!
            }
        }
    }

    private fun upsertUser(accessToken: String): UserEntity { // code smell
        val loginInfo = yandexOAuth2LoginInfoClient
            .getLoginInfo(accessToken)
            .block()

        requireNotNull(loginInfo)

        var userCreated = false

        val user = userRepository.findFirstByEmail(loginInfo.defaultEmail)?.apply {
            avatarUrl = loginInfo.avatarUrl
            realName = loginInfo.realName
        } ?: run {
            userCreated = true

            UserEntity(
                email = loginInfo.defaultEmail,
                realName = loginInfo.realName,
                avatarUrl = loginInfo.avatarUrl
            )
        }

        return userRepository.saveAndFlush(user).also {
            logger.info { "User ${if (userCreated) "created" else "updated"}" }
        }
    }

    private fun getOAuthTokenMeta(key: String) =
        oauthStates[key] ?: throw InvalidStateException("Provided stateId is invalid. Forbidden")
}
