package dev.rkashapov.security.auth.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import dev.rkashapov.base.logging.MdcKey
import dev.rkashapov.base.logging.withLoggingContext
import dev.rkashapov.security.oauth.api.model.OAuthProvider
import dev.rkashapov.security.oauth.service.YandexOAuthService
import jakarta.servlet.http.HttpServletRequest
import mu.KLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.chermenin.ua.UserAgent
import java.util.UUID

@RestController
@RequestMapping("/api/v1/oauth")
class OAuthController(
    private val objectMapper: ObjectMapper,
    private val yandexOAuthService: YandexOAuthService
) : KLogging() {

    @PostMapping("/link")
    fun getOAuth2Link(
        @RequestParam(defaultValue = "YANDEX") provider: OAuthProvider,
        @RequestHeader(name = "X_REQUEST_ID", required = true) requestId: UUID,
        request: HttpServletRequest
    ): ResponseEntity<String> {
        return withLoggingContext(MdcKey.REQUEST_ID to requestId) {

            logger.info { "Requested oauth2 link for $provider" }

            val userAgentValue = request.getHeader(HttpHeaders.USER_AGENT)
                ?: return ResponseEntity.badRequest().body(
                    objectMapper.writeValueAsString(mapOf("message" to "User-Agent header must be provided"))
                )

            return when (provider) {
                OAuthProvider.YANDEX -> yandexOAuthService.getOAuth2Link(UserAgent.parse(userAgentValue).toString())
                else -> return ResponseEntity.badRequest().body(
                    objectMapper.writeValueAsString(mapOf("message" to "Unknown oauth provider: $provider"))
                )
            }.let {
                ResponseEntity.ok(it)
            }
        }
    }

    @Transactional
    @GetMapping("/yandex/code-callback")
    fun processYandexCodeCallback() {

    }



}
