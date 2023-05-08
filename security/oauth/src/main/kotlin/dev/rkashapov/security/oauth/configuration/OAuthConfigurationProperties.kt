package dev.rkashapov.security.oauth.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "oauth")
data class OAuthConfigurationProperties(
    val stateKey: String,
    val issuer: String
)
