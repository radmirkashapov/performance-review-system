package dev.rkashapov.user.service

import dev.rkashapov.user.repository.UserRepository
import mu.KLogging
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : KLogging(), UserDetailsService {

    @Throws(UsernameNotFoundException::class, IllegalArgumentException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        logger.debug { "request: $username" }

        val user = try {
            userRepository.getReferenceById(UUID.fromString(username))
        } catch (e: IllegalArgumentException) {
            checkNotNull(userRepository.findFirstByEmail(username))
        }
        logger.trace { "User is found: $user" }

        return User
            .withUsername(username)
            .password("password") // FIXME
            .authorities(listOf(SimpleGrantedAuthority(user.role.name)))
            .build()
    }

}

