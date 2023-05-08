package dev.rkashapov.security.core.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "auth-jwt")
data class AuthJWTProperties(
    val issuer: String,
    val tokens: Tokens
) {
    data class Tokens(
        val access: Token,
        val refresh: Token
    )

    data class Token(
        val ttlInSeconds: Long,
        val key: String
    )
}

