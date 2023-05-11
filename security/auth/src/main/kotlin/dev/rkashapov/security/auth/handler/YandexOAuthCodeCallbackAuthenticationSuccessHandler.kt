package dev.rkashapov.security.auth.handler

import com.fasterxml.jackson.databind.ObjectMapper
import dev.rkashapov.base.model.UserRole
import dev.rkashapov.security.core.model.TokenRequest
import dev.rkashapov.security.core.service.AuthJWTService
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KLogging
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class YandexOAuthCodeCallbackAuthenticationSuccessHandler(
    private val authJWTService: AuthJWTService,
    private val objectMapper: ObjectMapper
) : KLogging(), AuthenticationSuccessHandler {

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(
        req: HttpServletRequest,
        res: HttpServletResponse,
        auth: Authentication
    ) {
        logger.debug { "auth: $auth" }

        val username = auth.principal.toString()
        logger.trace { "username: $username" }

        val userRole = UserRole.valueOf(auth.authorities.first().authority)

        res.contentType = MediaType.APPLICATION_JSON_VALUE
        res.writer.println(
            objectMapper.writeValueAsString(
                authJWTService.createToken(
                    TokenRequest(
                        userId = username,
                        role = userRole
                    )
                )
            )
        )
    }


}
