package dev.rkashapov.security.core.model

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import java.util.*

class CustomAuthenticationToken(
    private val authorities: List<GrantedAuthority> = emptyList(),
    private val principal: UUID,
    private val authenticated: Boolean
) : AbstractAuthenticationToken(authorities) {

    init {
        super.setAuthenticated(authenticated)
    }

    override fun getCredentials(): Any = throw NotImplementedError("Credentials not supported")

    override fun getPrincipal(): Any = principal
}
