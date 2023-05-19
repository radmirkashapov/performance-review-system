package dev.rkashapov.security.core.service

import dev.rkashapov.base.logging.MdcKey
import dev.rkashapov.base.logging.withLoggingContext
import dev.rkashapov.security.core.exception.NoAuthenticationFoundException
import dev.rkashapov.security.core.exception.NotAuthorizedException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import mu.KLogging
import org.springframework.stereotype.Service
import java.security.Key
import java.time.Instant
import java.util.*
import javax.crypto.SecretKey

@Service
class JWTService : KLogging() {

    fun generateToken(
        subject: String,
        claims: Map<String, String> = mapOf(),
        issuer: String,
        issuedAt: Instant,
        expiresAt: Instant,
        signingKey: SecretKey,
        requestId: String? = null
    ): String {
        return withLoggingContext(MdcKey.REQUEST_ID to requestId) {
            logger.info { "Generating token [subject=$subject, claims=$claims, expires=$expiresAt]" }

            Jwts.builder()
                .signWith(signingKey)
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(issuer)
                .setIssuedAt(Date.from(issuedAt))
                .setExpiration(Date.from(expiresAt))
                .compact()
        }
    }

    fun validateAndParseToken(token: String, key: Key, requestId: String? = null): Claims {
        return withLoggingContext(MdcKey.REQUEST_ID to requestId) {
            logger.info { "Validating token..." }

            try {
                val jws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)

                logger.trace { "Jws: $jws" }

                jws.body
            } catch (e: ExpiredJwtException) {
                throw NotAuthorizedException()
            } catch (e: JwtException) {
                logger.error(e) { "Token with exception" }

                throw NoAuthenticationFoundException("Token parse failed")
            }
        }
    }


}
