package dev.rkashapov.security.core.service

import dev.rkashapov.security.core.exception.NoAuthenticationFoundException
import dev.rkashapov.security.core.model.CurrentUser
import mu.KLogging
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class AuthorizationService : KLogging() {

    @Throws(NoAuthenticationFoundException::class)
    fun currentUserOrDie(authentication: Authentication = getAuthentication()): CurrentUser {
        logger.trace { "authentication: $authentication" }

        return checkNotNull(authentication.details as? CurrentUser) {
            "Current user is not found in session"
        }
    }

    fun getAuthentication() = checkNotNull(SecurityContextHolder.getContext().authentication) {
        "authentication is null"
    }


}
