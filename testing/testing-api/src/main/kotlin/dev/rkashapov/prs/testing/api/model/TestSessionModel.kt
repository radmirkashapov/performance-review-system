package dev.rkashapov.prs.testing.api.model

import java.time.Instant
import java.util.*

data class TestSessionModel(
    val id: UUID,
    val testId: UUID,
    val startedAt: Instant,
    val checkListId: UUID? = null
)
