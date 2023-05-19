package dev.rkashapov.security.core.model

import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.*

data class CurrentUser(
    val id: UUID,
    val enabled: Boolean,
    val accountNonLocked: Boolean,
    val authorities: List<SimpleGrantedAuthority>
)
