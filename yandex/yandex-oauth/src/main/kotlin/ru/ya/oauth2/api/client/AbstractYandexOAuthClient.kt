package ru.ya.oauth2.api.client

import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import ru.ya.oauth2.api.exception.YandexOAuthWebClientException
import java.util.function.Function

abstract class AbstractYandexOAuthClient : KLogging() {
    protected inline fun <reified T : Any> defaultErrorProcessing() =
        Function<WebClientResponseException, Mono<T>> { ex ->
            val responseBodyAsString = ex.responseBodyAsString
            Mono.error(
                when (ex.statusCode) {
                    HttpStatus.BAD_REQUEST -> {

                        logger.error(ex) { "Error during Yandex request" }

                        YandexOAuthWebClientException(
                            throwable = ex,
                            responseAsString = responseBodyAsString
                        )
                    }

                    else -> YandexOAuthWebClientException(responseAsString = responseBodyAsString)
                }
            )
        }
}
