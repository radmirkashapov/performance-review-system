package ru.ya.oauth2.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class YandexOauth2WebClientConfiguration {


    @Bean
    fun yandexWebSecurityOAuth2Client(): WebClient {
        return WebClient
            .builder()
            .baseUrl("https://oauth.yandex.ru") // TODO to prop file
            .build()
    }

}
