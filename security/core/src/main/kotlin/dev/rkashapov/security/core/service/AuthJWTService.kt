package dev.rkashapov.security.core.service

import dev.rkashapov.base.logging.MdcKey
import dev.rkashapov.base.logging.withLoggingContext
import dev.rkashapov.base.model.Token
import dev.rkashapov.security.core.configuration.AuthJWTProperties
import dev.rkashapov.security.core.model.AuthToken
import dev.rkashapov.security.core.model.TokenRequest
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import mu.KLogging
import org.springframework.stereotype.Service
import java.security.Key
import java.time.Instant

@Service
class AuthJWTService(
    private val jwtService: JWTService,
    private val jwtProperties: AuthJWTProperties
) : KLogging() {

    companion object {
        private const val refreshClaims = "REFRESH"
    }

    private val algorithmForAccessKey = checkNotNull(Keys.hmacShaKeyFor(jwtProperties.tokens.access.key.toByteArray()))
    private val algorithmForRefreshKey = checkNotNull(Keys.hmacShaKeyFor(jwtProperties.tokens.refresh.key.toByteArray()))


    fun createToken(request: TokenRequest): Token {
        logger.debug { "request: $request" }

        val now = Instant.now()

        logger.info { "Generating JWT token pair. Request: $request" }
        return AuthToken(
            accessToken = generateAccessToken(request, now),
            refreshToken = generateRefreshToken(request, now)
        )
    }

    fun validateAndParseAccessToken(token: String) =
        jwtService.validateAndParseToken(token, algorithmForAccessKey)

    fun validateAndParseRefreshToken(token: String) =
        jwtService.validateAndParseToken(token, algorithmForRefreshKey)

    private fun generateAccessToken(
        request: TokenRequest,
        issuedAt: Instant,
    ): String = withLoggingContext(MdcKey.USER_ID to request.userId) {
        val expiresAt = Instant.now().plusSeconds(jwtProperties.tokens.access.ttlInSeconds)

        val token = jwtService.generateToken(
            subject = request.userId,
            claims = mapOf("roles" to "ROLE_${request.role}"),
            issuedAt = issuedAt,
            expiresAt = expiresAt,
            issuer = jwtProperties.issuer,
            signingKey = algorithmForAccessKey
        )

        logger.trace { "Token: $token" }

        return token
    }

    private fun generateRefreshToken(
        request: TokenRequest,
        issuedAt: Instant,
    ): String = withLoggingContext(MdcKey.USER_ID to request.userId) {
        val expiresAt = Instant.now().plusSeconds(jwtProperties.tokens.refresh.ttlInSeconds)

        val token = jwtService.generateToken(
            subject = request.userId,
            claims = mapOf("roles" to refreshClaims),
            issuedAt = issuedAt,
            expiresAt = expiresAt,
            issuer = jwtProperties.issuer,
            signingKey = algorithmForRefreshKey
        )

        logger.trace { "Token: $token" }

        return token
    }



}
