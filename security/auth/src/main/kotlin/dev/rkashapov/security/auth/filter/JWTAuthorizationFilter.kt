package dev.rkashapov.security.auth.filter

import dev.rkashapov.base.security.SecurityConstants.BEARER_TOKEN_PREFIX
import dev.rkashapov.security.auth.configuration.WebSecurityConfiguration
import dev.rkashapov.security.core.exception.NoAuthenticationFoundException
import dev.rkashapov.security.core.exception.NotAuthorizedException
import dev.rkashapov.security.core.model.CustomAuthenticationToken
import dev.rkashapov.security.core.service.AuthJWTService
import dev.rkashapov.user.service.CustomUserDetailsService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpMethod
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JWTAuthorizationFilter(
    private val userDetailsService: CustomUserDetailsService,
    private val authJWTService: AuthJWTService
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return request.requestURI.startsWith("/api/v1/refresh-token") ||
                (request.requestURI.startsWith(WebSecurityConfiguration.DEFAULT_LOGIN_URL) && request.method == HttpMethod.POST.name())
    }

    @Throws(NotAuthorizedException::class, NoAuthenticationFoundException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        logger.trace("request: ${request.requestURI}?${request.queryString}")

        val tokenWithHeader = request.getHeader(AUTHORIZATION)?.takeIf { it.startsWith("$BEARER_TOKEN_PREFIX ") }
            ?: run {
                chain.doFilter(request, response)
                return
            }

        try {
            val claims =
                authJWTService.validateAndParseAccessToken(token = tokenWithHeader.replace("$BEARER_TOKEN_PREFIX ", ""))
            logger.debug("Claims: $claims")

            val userId = claims.subject
            logger.debug("UserId: $userId")

            val userDetails = userDetailsService.loadUserByUsername(userId)

            val authenticationToken = CustomAuthenticationToken(
                userDetails.authorities,
                userDetails.id,
                true
            )

            authenticationToken.details = userDetails

            SecurityContextHolder.getContext().authentication = authenticationToken
        } catch (e: NotAuthorizedException) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED

            return
        } catch (e: NoAuthenticationFoundException) {
            logger.error("request path: ${request.requestURI}")

            return
        }

        chain.doFilter(request, response)
    }


}
