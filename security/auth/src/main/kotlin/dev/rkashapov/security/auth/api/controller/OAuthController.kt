package dev.rkashapov.security.auth.api.controller

import dev.rkashapov.base.logging.CustomHeaders
import dev.rkashapov.base.logging.MdcKey
import dev.rkashapov.base.logging.withLoggingContext
import dev.rkashapov.security.auth.api.model.OAuthLinkModel
import dev.rkashapov.security.oauth.api.model.OAuthProvider
import dev.rkashapov.security.oauth.api.model.YandexOAuthCodeModel
import dev.rkashapov.security.oauth.service.YandexOAuthService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import mu.KLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import ru.chermenin.ua.UserAgent
import java.util.*

@RestController
@RequestMapping("/api/v1/oauth")
class OAuthController(
    private val yandexOAuthService: YandexOAuthService
) : KLogging() {

    @GetMapping("/link")
    fun getOAuth2Link(
        @RequestParam(defaultValue = "YANDEX") provider: OAuthProvider,
        @RequestHeader(name = CustomHeaders.X_DEVICE_ID, required = true) deviceId: String,
        @RequestHeader(name = CustomHeaders.X_REQUEST_ID, required = false) requestId: String?,
        request: HttpServletRequest
    ): ResponseEntity<OAuthLinkModel> {
        return withLoggingContext(MdcKey.REQUEST_ID to requestId, MdcKey.DEVICE_ID to deviceId) {

            logger.info { "Requested oauth2 link for $provider" }

            val userAgentValue = request.getHeader(HttpHeaders.USER_AGENT)
                ?: return ResponseEntity.badRequest().build()

            when (provider) {
                OAuthProvider.YANDEX -> yandexOAuthService.getOAuth2Link(
                    UserAgent.parse(userAgentValue).toString(),
                    deviceId,
                    requestId
                )

                else -> return ResponseEntity.badRequest().build()
            }.let {
                ResponseEntity.ok(OAuthLinkModel(link = it))
            }
        }
    }

    @Transactional
    @PostMapping("/yandex/code-login-callback")
    fun processYandexCodeCallback(@Valid @RequestBody body: YandexOAuthCodeModel) {
        throw IllegalStateException("Add Spring Security to handle authentication")
    }


}
