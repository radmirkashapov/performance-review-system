package dev.rkashapov.security.core.exception

import org.springframework.security.core.AuthenticationException

class NoAuthenticationFoundException(override val message: String = "No authentication found") :
    AuthenticationException(message)

