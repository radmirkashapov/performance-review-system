package ru.ya.oauth2.api.client

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import ru.ya.oauth2.api.model.YandexOAuth2TokenRes
import ru.ya.oauth2.configuration.YandexOAuth2ConfigurationProperties
import java.net.URLEncoder

@Component
class YandexOAuth2TokenClient(
    private val properties: YandexOAuth2ConfigurationProperties,
    private val yandexSecurityOAuth2WebClient: WebClient,
    private val objectMapper: ObjectMapper
) : AbstractYandexOAuthClient() {

    fun buildUrlForUser(deviceId: String, deviceName: String, state: String): String {
        return UriComponentsBuilder
            .fromHttpUrl("https://oauth.yandex.ru/authorize?response_type=code")
            .apply {
                queryParam("client_id", properties.clientId)
                queryParam("device_id", deviceId)
                queryParam("device_name", URLEncoder.encode(deviceName, Charsets.UTF_8))
                queryParam("redirect_uri", properties.redirectUri)

                if (properties.scope.isNotEmpty()) {
                    queryParam("scope", properties.scope)
                }

                queryParam("force_confirm", true)
                queryParam("state", state)
            }
            .build()
            .toUriString()

    }

    fun swapCodeToToken(code: String, deviceId: String, deviceName: String): Mono<YandexOAuth2TokenRes> {
        return yandexSecurityOAuth2WebClient
            .post()
            .uri("/token")
            .contentType(APPLICATION_FORM_URLENCODED)
            .body(
                BodyInserters
                    .fromFormData("grant_type", "authorization_code")
                    .with("code", code)
                    .with("device_id", deviceId)
                    .with("device_name", deviceName)
            )
            .accept(APPLICATION_JSON)
            .retrieve()
            .bodyToMono<YandexOAuth2TokenRes>()
            .onErrorResume(WebClientResponseException::class.java, defaultErrorProcessing())
    }

    fun refreshTokens(refreshToken: String): Mono<YandexOAuth2TokenRes> {
        return yandexSecurityOAuth2WebClient
            .post()
            .uri("/token")
            .contentType(APPLICATION_FORM_URLENCODED)
            .body(
                BodyInserters
                    .fromFormData("grant_type", "refresh_token")
                    .with("refresh_token", refreshToken)
            )
            .accept(APPLICATION_JSON)
            .retrieve()
            .bodyToMono<YandexOAuth2TokenRes>()
            .onErrorResume(WebClientResponseException::class.java, defaultErrorProcessing())
    }
}
