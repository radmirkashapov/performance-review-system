package ru.ya.oauth2.api.exception

import org.springframework.web.reactive.function.client.WebClientException

class YandexOAuthWebClientException : WebClientException {
    constructor(message: String = DEFAULT_MSG, throwable: Throwable, responseAsString: String) : super(
        message,
        throwable
    ) {
        this.responseAsString = responseAsString
    }

    constructor(message: String = DEFAULT_MSG) : super(message)

    constructor(message: String = DEFAULT_MSG, responseAsString: String) : super(message) {
        this.responseAsString = responseAsString
    }

    private lateinit var responseAsString: String

    companion object {
        const val DEFAULT_MSG = "Bad request. Retry in a while"
    }

}
