package dev.rkashapov.security.oauth.api.model

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

data class YandexOAuthCodeModel(
    val code: String,
    val state: String
) : UsernamePasswordAuthenticationToken(state, code)
