package dev.rkashapov.security.core.exception

import mu.KLoggable
import mu.KLogger
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler(), KLoggable {

    @Suppress("LeakingThis")
    override val logger: KLogger = logger()

    @ExceptionHandler(NoAuthenticationFoundException::class, NotAuthorizedException::class)
    fun handleUnauthorized(ex: Exception): Nothing = handle(ex, HttpStatus.UNAUTHORIZED)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: Exception): Nothing = handle(ex, HttpStatus.BAD_REQUEST)

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(ex: ResponseStatusException): Nothing =
        handle(ex, HttpStatus.valueOf(ex.statusCode.value()))

    @ExceptionHandler(Exception::class)
    fun handleInternalServerError(ex: Exception): Nothing = handle(ex, HttpStatus.INTERNAL_SERVER_ERROR)

    private fun handle(ex: Exception, status: HttpStatus): Nothing {
        logger.trace("Exception handling", ex)
        throw HttpClientErrorException(status)
    }
}
