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

    @JsonProperty(value = "is_avatar_empty")
    val isAvatarEmpty: Boolean = true,

    @JsonProperty(value = "default_avatar_id")
    val defaultAvatarId: String,

    @JsonProperty(value = "real_name")
    val realName: String
) {
    /**
     * @see [Documentation](https://yandex.ru/dev/id/doc/ru/user-information)
     * */
    val avatarUrl = "https://avatars.yandex.net/get-yapic/$defaultAvatarId/islands-50"
}
