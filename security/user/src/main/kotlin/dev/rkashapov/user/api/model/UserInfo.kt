package dev.rkashapov.user.api.model

import dev.rkashapov.base.model.UserRole
import java.util.*

data class UserInfo(
    val id: UUID,
    val realName: String,
    val authorities: List<UserRole> = emptyList()
)
