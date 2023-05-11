package dev.rkashapov.security.auth.api.model

import jakarta.validation.constraints.NotBlank

data class RefreshTokenRequest(
    @field:NotBlank
    val refreshToken: String
)
