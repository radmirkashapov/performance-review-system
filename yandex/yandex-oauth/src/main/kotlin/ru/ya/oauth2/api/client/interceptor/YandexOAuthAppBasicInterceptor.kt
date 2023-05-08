package ru.ya.oauth2.api.client.interceptor

import dev.rkashapov.base.security.SecurityConstants
import dev.rkashapov.base.security.SecurityConstants.BASIC_TOKEN_PREFIX
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import reactor.core.publisher.Mono
import ru.ya.oauth2.configuration.YandexOAuth2ConfigurationProperties
import java.util.*

@Component
class YandexOAuthAppBasicInterceptor(
    private val properties: YandexOAuth2ConfigurationProperties
) : ExchangeFilterFunction {
    override fun filter(request: ClientRequest, next: ExchangeFunction): Mono<ClientResponse> {
        return next.exchange(
            ClientRequest
                .from(request)
                .headers {
                    it.setBasicAuth(properties.clientId, properties.clientSecret)
                }
                .build()
        )
    }
}
