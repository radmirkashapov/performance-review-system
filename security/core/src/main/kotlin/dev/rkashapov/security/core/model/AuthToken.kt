package dev.rkashapov.security.core.model

import dev.rkashapov.base.model.Token

data class AuthToken(
    override val accessToken: String,
    override val refreshToken: String
) : Token
