package ru.ya.oauth2.configuration

import dev.rkashapov.base.factory.YamlPropertySourceFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.PropertySource

@PropertySource("classpath:yandex-oauth-properties.yml", factory = YamlPropertySourceFactory::class)
@ConfigurationProperties(prefix = "yandex.oauth", ignoreUnknownFields = true)
class YandexOAuth2ConfigurationProperties(
    val clientId: String,
    val clientSecret: String,
    val redirectUri: List<String>,
    val scope: List<String>
)
