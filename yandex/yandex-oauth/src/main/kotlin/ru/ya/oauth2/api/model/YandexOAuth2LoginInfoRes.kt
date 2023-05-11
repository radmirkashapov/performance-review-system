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
     *
     * islands-small — 28×28px.
     * islands-34 — 34×34px.
     * islands-middle — 42×42px.
     * islands-50 — 50×50px.
     * islands-retina-small — 56×56px.
     * islands-68 — 68×68px.
     * islands-75 — 75×75px.
     * islands-retina-middle — 84×84px.
     * islands-retina-50 — 100×100px.
     * islands-200 — 200×200px.
     * @see [Documentation](https://yandex.ru/dev/id/doc/ru/user-information)
     * */
    val avatarUrl = "https://avatars.yandex.net/get-yapic/$defaultAvatarId/<replace-with-picture-size>"
}
