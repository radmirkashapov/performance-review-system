package ru.ya.oauth2.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "yandex.oauth2")
class YandexOAuth2ConfigurationProperties(
    val clientId: String,
    val clientSecret: String,
    val redirectUri: List<String>,
    val scope: List<String>
)
