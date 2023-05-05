package ru.ya.oauth2.api.client

import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
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

    fun swapCodeToToken(code: String, deviceId: UUID, deviceName: String) {
        yandexWebSecurityOAuth2Client
            .post()
            .uri {
                it
                    .path("/token")
                    .queryParam("grant_type", "authorization_code")
                    .queryParam("code", code)
                    .queryParam("device_id", deviceId)
                    .queryParam("device_name", deviceName)
                    .build()
            }
            .contentType(APPLICATION_FORM_URLENCODED)
            .accept(APPLICATION_JSON)
            .header(
                AUTHORIZATION,
                "Basic ${
                    Base64.getEncoder()
                        .encodeToString("${properties.clientId}:${properties.clientSecret}".encodeToByteArray())
                }"
            )
    }

}
