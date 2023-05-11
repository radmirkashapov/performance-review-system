package dev.rkashapov.base.logging

import org.slf4j.MDC

inline fun <T> withLoggingContext(
    vararg pairs: Pair<MdcKey, Any?>,
    body: () -> T
): T {
    val originalMap = MDC.getCopyOfContextMap() ?: emptyMap()
    val newValues = pairs.filter { it.second != null }.associate { it.first.name to it.second.toString() }
    val newMap = originalMap + newValues

    try {
        MDC.setContextMap(newMap)
        return body()
    } finally {
        MDC.setContextMap(originalMap)
    }
}

