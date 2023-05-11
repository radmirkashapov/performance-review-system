package dev.rkashapov.security.oauth.exception

class InvalidStateException : Exception {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
