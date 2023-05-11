package ru.ya.oauth2.api.client

import dev.rkashapov.base.security.SecurityConstants.OAUTH_TOKEN_PREFIX
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import ru.ya.oauth2.api.model.YandexOAuth2LoginInfoRes

@Component
class YandexOAuth2LoginInfoClient(
    private val yandexOAuth2LoginInfoWebClient: WebClient,
) : AbstractYandexOAuthClient() {

    fun getLoginInfo(token: String): Mono<YandexOAuth2LoginInfoRes> {
        return yandexOAuth2LoginInfoWebClient
            .get()
            .uri {
                it
                    .path("/info")
                    .queryParam("format", "json")
                    .build()
            }
            .header(AUTHORIZATION, "$OAUTH_TOKEN_PREFIX $token")
            .retrieve()
            .bodyToMono<YandexOAuth2LoginInfoRes>()
            .onErrorResume(WebClientResponseException::class.java, defaultErrorProcessing())
    }

}
