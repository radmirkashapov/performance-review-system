package dev.rkashapov.base.retry

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds

fun <R> retryRunBlocking(
    maxAttempts: Int,
    delayInSeconds: Long,
    nearActionWithDelay: Boolean,
    action: suspend () -> R
): R {
    require(maxAttempts > 0) { "maxAttempts must be greater than 0" }
    return runCatching {
        runBlocking {
            if (nearActionWithDelay) {
                delay(delayInSeconds.seconds)
            }

            action()
        }
    }.getOrElse {
        val leftAttempts = maxAttempts.dec()
        if (leftAttempts == 0) throw it
        retryRunBlocking(
            maxAttempts = leftAttempts,
            delayInSeconds = delayInSeconds,
            nearActionWithDelay = true,
            action
        )
    }
}
