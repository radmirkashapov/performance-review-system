package dev.rkashapov.security.oauth.configuration

import dev.rkashapov.base.factory.YamlPropertySourceFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.PropertySource

@ConfigurationProperties(prefix = "oauth")
@PropertySource("classpath:oauth-properties.yml", factory = YamlPropertySourceFactory::class)
data class OAuthConfigurationProperties(
    val stateKey: String,
    val issuer: String
)
