package ru.ya.oauth2.api.client

import dev.rkashapov.base.security.SecurityConstants.BASIC_TOKEN_PREFIX
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.util.UriBuilder
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import ru.ya.oauth2.api.model.YandexOAuth2TokenRes
import ru.ya.oauth2.configuration.YandexOAuth2ConfigurationProperties
import java.util.Base64
import java.util.UUID

@Component
class YandexOAuth2Client(
    private val properties: YandexOAuth2ConfigurationProperties,
    private val yandexWebSecurityOAuth2Client: WebClient
) {

    fun buildUrlForUser(deviceId: UUID, deviceName: String, stateId: UUID): String {
        return UriComponentsBuilder
            .fromHttpUrl("https://oauth.yandex.ru/authorize?response_type=code")
            .apply {
                queryParam("clientId", properties.clientId)
                queryParam("device_id", deviceId)
                queryParam("device_name", deviceName)
                queryParam("redirect_uri", properties.redirectUri)
                queryParam("scope", properties.scope)
                queryParam("force_confirm", true)
                queryParam("state", stateId)
            }
            .build()
            .toUriString()

    }

    fun swapCodeToToken(code: String, deviceId: UUID, deviceName: String): Mono<YandexOAuth2TokenRes> {
        return makeTokenRequest {
            initSwapCodeToTokenRequestBuilder(it, code, deviceId, deviceName)
        }
    }

    fun refreshTokens(refreshToken: String): Mono<YandexOAuth2TokenRes> {
        return makeTokenRequest {
            initRefreshTokenRequestBuilder(it, refreshToken)
        }
    }

    private fun makeTokenRequest(initUriBuilder: (UriBuilder) -> Unit): Mono<YandexOAuth2TokenRes> {
        return yandexWebSecurityOAuth2Client
            .post()
            .uri {
                initUriBuilder(it)
                it.build()
            }
            .contentType(APPLICATION_FORM_URLENCODED)
            .accept(APPLICATION_JSON)
            .header(
                AUTHORIZATION,
                "$BASIC_TOKEN_PREFIX ${
                    Base64.getEncoder()
                        .encodeToString("${properties.clientId}:${properties.clientSecret}".encodeToByteArray())
                }"
            )
            .retrieve()
            .bodyToMono()
    }

    private fun initSwapCodeToTokenRequestBuilder(
        uriBuilder: UriBuilder,
        code: String,
        deviceId: UUID,
        deviceName: String
    ): UriBuilder {
        return uriBuilder
            .path("/token")
            .queryParam("grant_type", "authorization_code")
            .queryParam("code", code)
            .queryParam("device_id", deviceId)
            .queryParam("device_name", deviceName)
    }

    private fun initRefreshTokenRequestBuilder(
        uriBuilder: UriBuilder,
        refreshToken: String
    ): UriBuilder {
        return uriBuilder
            .path("/token")
            .queryParam("grant_type", "refresh_token")
            .queryParam("refresh_token", refreshToken)
    }

}
