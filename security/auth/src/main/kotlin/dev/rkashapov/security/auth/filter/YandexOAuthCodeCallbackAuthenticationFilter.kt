package dev.rkashapov.security.auth.filter

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dev.rkashapov.base.logging.CustomHeaders
import dev.rkashapov.security.auth.configuration.WebSecurityConfiguration
import dev.rkashapov.security.oauth.api.model.YandexOAuthCodeModel
import dev.rkashapov.security.oauth.service.YandexOAuthService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.server.ResponseStatusException
import java.util.*

class YandexOAuthCodeCallbackAuthenticationFilter(
    private val objectMapper: ObjectMapper,
    private val yandexOAuthService: YandexOAuthService
) : AbstractAuthenticationProcessingFilter(
    YandexOAuthCodeCallbackRequestMatcher(WebSecurityConfiguration.DEFAULT_LOGIN_URL, HttpMethod.POST)
) {

    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(
        req: HttpServletRequest,
        res: HttpServletResponse
    ): Authentication {
        val bodyRequest = requireNotNull((req.inputStream).use { ris -> ris.bufferedReader().use { it.readText() } })

        val requestId = req.getHeader(CustomHeaders.X_REQUEST_ID)

        val oauthCode = try {
            objectMapper.readValue<YandexOAuthCodeModel>(bodyRequest)
        } catch (e: JsonProcessingException) {
            logger.error("Request body: $bodyRequest", e)
            null
        } catch (e: JsonMappingException) {
            logger.error("Request body: $bodyRequest", e)
            null
        } ?: throw ResponseStatusException(UNAUTHORIZED, "Json processing failed")

        val user = try {
            yandexOAuthService.processYandexCodeCallback(
                stateId = oauthCode.state,
                code = oauthCode.code,
                requestId = requestId
            )
        } catch (e: Exception) {
            logger.error("Yandex callback processing failed", e)
            null
        } ?: throw ResponseStatusException(UNAUTHORIZED, "Yandex callback processing failed")

        val authentication = UsernamePasswordAuthenticationToken(
            user.emailEncoded,
            null,
            user.role.let {
                listOf(
                    SimpleGrantedAuthority(it.name)
                )
            }
        )

        authentication.details = WebAuthenticationDetailsSource().buildDetails(req)

        logger.trace("authentication: $authentication")

        return authentication
    }


}
