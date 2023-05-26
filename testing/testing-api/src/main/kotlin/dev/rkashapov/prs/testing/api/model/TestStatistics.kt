package dev.rkashapov.prs.testing.api.model

import java.time.Instant
import java.util.*

sealed interface TestStatistics {
    val testId: UUID
    val testName: String
    val sessionId: UUID
    val startedAt: Instant
    val finishedAt: Instant
}
