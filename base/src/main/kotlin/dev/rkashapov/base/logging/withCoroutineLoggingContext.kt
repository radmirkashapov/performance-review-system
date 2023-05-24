package dev.rkashapov.base.logging

import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.slf4j.MDC

suspend fun <T> withCoroutineLoggingContext(
    vararg pairs: Pair<MdcKey, Any?>,
    body: suspend () -> T
): T {
    val originalMap = MDC.getCopyOfContextMap() ?: emptyMap()
    val newValues = pairs.filter { it.second != null }.associate { it.first.name to it.second.toString() }
    val newMap = originalMap + newValues

    // MDCContext сам запомнит и восстановит старые значения
    return withContext(MDCContext(newMap)) {
        body()
    }
}
