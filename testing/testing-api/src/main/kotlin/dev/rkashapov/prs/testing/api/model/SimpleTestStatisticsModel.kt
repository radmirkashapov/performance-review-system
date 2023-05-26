package dev.rkashapov.prs.testing.api.model

import java.time.Instant
import java.util.*

data class SimpleTestStatisticsModel(
    override val testName: String,
    override val testId: UUID,
    override val sessionId: UUID,
    override val startedAt: Instant,
    override val finishedAt: Instant
) : TestStatistics
