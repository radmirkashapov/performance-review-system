package dev.rkashapov.security.auth.configuration

import mu.KLogging
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "encrypt", ignoreInvalidFields = false)
data class EncryptProperties(
    val keys: Map<String, String>
) : KLogging() {
    fun getSql(): String = keys.map { "SET encrypt.${it.key} = ${it.value};" }
        .joinToString("")
        .also { logger.trace { "init sql: $it" } }
}
