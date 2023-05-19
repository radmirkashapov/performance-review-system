package dev.rkashapov.security.auth.api.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

data class RefreshTokenRequest(
    @field:NotBlank(message = "Refresh token must be provided in body")
    @field:JsonProperty("refreshToken")
    val refreshToken: String
)
