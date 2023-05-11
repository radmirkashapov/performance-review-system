package dev.rkashapov.security.auth.api.controller

import dev.rkashapov.base.model.Token
import dev.rkashapov.security.auth.api.model.RefreshTokenRequest
import dev.rkashapov.security.core.model.TokenRequest
import dev.rkashapov.security.core.service.AuthJWTService
import dev.rkashapov.user.repository.UserRepository
import io.swagger.v3.oas.annotations.parameters.RequestBody
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class AuthController(
    private val authJWTService: AuthJWTService,
    private val userRepository: UserRepository, // FIXME to service layer
) {

    @PostMapping("/api/v1/refresh-token")
    fun refreshTokens(@Valid @RequestBody request: RefreshTokenRequest): ResponseEntity<Token> {
        val claims = authJWTService.validateAndParseRefreshToken(request.refreshToken)
        val user = userRepository.findById(UUID.fromString(claims.subject)).get()
        return ResponseEntity.ok(
            authJWTService.createToken(
                TokenRequest(
                    userId = user.id!!.toString(),
                    role = user.role
                )
            )
        )
    }


}
