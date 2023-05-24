package dev.rkashapov.user.service

import dev.rkashapov.base.model.UserStatus.BLOCKED
import dev.rkashapov.base.model.UserStatus.DELETED
import dev.rkashapov.security.core.model.CurrentUser
import dev.rkashapov.user.repository.UserRepository
import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : KLogging() {

    @Throws(UsernameNotFoundException::class)
    fun loadUserByUsername(username: String): CurrentUser {
        logger.debug { "request: $username" }

        val user = try {
            userRepository.getReferenceById(UUID.fromString(username))
        } catch (e: IllegalArgumentException) {
            userRepository.findFirstByEmailEncoded(username)
                ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }
        logger.trace { "User is found: $user" }

        return CurrentUser(
            id = checkNotNull(user.id),
            enabled = user.status != DELETED,
            accountNonLocked = user.status != BLOCKED,
            authorities = listOf(SimpleGrantedAuthority(user.role.name))
        )
    }



}

