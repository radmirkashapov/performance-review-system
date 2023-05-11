package dev.rkashapov.security.core.model

import dev.rkashapov.base.model.UserRole

data class TokenRequest(
    val userId: String,
    val role: UserRole
)
