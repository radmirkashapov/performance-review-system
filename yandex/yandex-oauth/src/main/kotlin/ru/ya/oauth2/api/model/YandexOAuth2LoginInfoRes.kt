package ru.ya.oauth2.api.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class YandexOAuth2LoginInfoRes(
    val login: String,
    val id: String,
    @JsonProperty(value = "client_id")
    val clientId: String,
    val psuid: String,
    @JsonProperty(value = "default_email")
    val defaultEmail: String,

    @JsonProperty(value = "real_name")
    val realName: String
)
