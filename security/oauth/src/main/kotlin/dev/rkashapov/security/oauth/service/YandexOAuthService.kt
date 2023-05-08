package dev.rkashapov.security.oauth.service

import dev.rkashapov.base.logging.MdcKey
import dev.rkashapov.base.logging.withLoggingContext
import dev.rkashapov.base.security.AESService
import dev.rkashapov.security.oauth.configuration.OAuthConfigurationProperties
import dev.rkashapov.security.oauth.entity.OAuthTokenEntity
import dev.rkashapov.security.oauth.repository.OAuthTokenRepository
import dev.rkashapov.user.entity.UserEntity
import dev.rkashapov.user.repository.UserRepository
import korlibs.crypto.AES
import mu.KLogging
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.base64.Base64
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClientException
import ru.ya.oauth2.api.client.YandexOAuth2LoginInfoClient
import ru.ya.oauth2.api.client.YandexOAuth2TokenClient
import java.time.LocalDateTime
import java.util.UUID
import kotlin.math.log

@Service
class YandexOAuthService(
    private val yandexOAuth2TokenClient: YandexOAuth2TokenClient,
    private val oAuthTokenRepository: OAuthTokenRepository,
    private val yandexOAuth2LoginInfoClient: YandexOAuth2LoginInfoClient,
    private val userRepository: UserRepository,
    private val aesService: AESService,
    private val oAuthConfigurationProperties: OAuthConfigurationProperties
) : KLogging() {

    private val cipherKey = Base64.decode(oAuthConfigurationProperties.stateKey)

    @Transactional
    fun getOAuth2Link(deviceName: String, requestId: UUID): String {
        return withLoggingContext(MdcKey.REQUEST_ID to requestId) {
            logger.info { "Requested oauth2 link for YANDEX" }

            val deviceId = UUID.randomUUID()

            val state = oAuthTokenRepository.save(OAuthTokenEntity(deviceId = deviceId, deviceName = deviceName))

            logger.trace { "stateId: ${state.id}" }

            val base64StateIdEncrypted =
                Base64
                    .decode(state.id.toString())
                    .let {
                        val encrypted = aesService.encrypt(it, cipherKey)
                        Base64.toBase64String(encrypted)
                    }

            withLoggingContext() {
                yandexOAuth2TokenClient
                    .buildUrlForUser(
                        stateId = base64StateIdEncrypted,
                        deviceName = deviceName,
                        deviceId = deviceId
                    )
            }
        }
    }

    @Transactional(rollbackFor = [WebClientException::class])
    fun processYandexCodeCallback(base64StateIdEncrypted: String, code: String, requestId: UUID) {
        withLoggingContext(MdcKey.REQUEST_ID to requestId) {

            logger.info { "Processing yandex code callback" }

            val stateId = Base64
                .decode(base64StateIdEncrypted)
                .let {
                    val decrypted = aesService.decrypt(it, cipherKey)
                    UUID.fromString(Base64.decode(decrypted).contentToString())
                }

            withLoggingContext(MdcKey.USER_ID to stateId) {
                val state = oAuthTokenRepository.findById(stateId).orElseThrow()

                logger.trace { "state: $state" }
                val now = LocalDateTime.now()

                if (!state.isInitialized()) {

                    logger.info { "OAuth token haven't initialized yet or expired. Processing..." }

                    val token = yandexOAuth2TokenClient
                        .swapCodeToToken(code = code, deviceId = state.deviceId, deviceName = state.deviceName)
                        .block()

                    requireNotNull(token)

                    state.apply {
                        accessToken = token.accessToken
                        refreshToken = token.refreshToken
                        expiresIn = now.plusSeconds(token.expiresInSeconds).minusMinutes(1L)
                    }
                }

                val user = upsertUser(state, state.accessToken!!)
                state.owner = user

                oAuthTokenRepository.saveAndFlush(state)
            }
        }
    }

    private fun upsertUser(state: OAuthTokenEntity, accessToken: String): UserEntity { // code smell
        val loginInfo = yandexOAuth2LoginInfoClient
            .getLoginInfo(accessToken)
            .block()

        requireNotNull(loginInfo)

        val user = state.owner.takeIf { state.isInitialized() } ?: UserEntity(
            email = loginInfo.defaultEmail,
            realName = loginInfo.realName
        )

        return userRepository.saveAndFlush(
            user.apply {
                this.avatarUrl = loginInfo.avatarUrl
            }
        ).also {
            logger.info { "User ${if (state.isInitialized()) "updated" else "created"}" }
        }
    }
}
