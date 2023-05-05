package ru.ya.oauth2.api.model

import com.fasterxml.jackson.annotation.JsonProperty

data class YandexOAuth2TokenRes(
    @JsonProperty(value = "access_token")
    val accessToken: String,

    @JsonProperty(value = "refresh_token")
    val refreshToken: String,

    @JsonProperty(value = "token_type")
    val tokenType: String,

    @JsonProperty(value = "expires_in")
    val expiresInSeconds: Long
)
