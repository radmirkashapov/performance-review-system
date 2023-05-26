package dev.rkashapov.prs.testing.api.model

import java.time.Instant
import java.util.*

data class ExtendedTestStatisticsModel(
    override val testId: UUID,
    override val testName: String,
    override val sessionId: UUID,
    override val startedAt: Instant,
    override val finishedAt: Instant,
    val difficultyChange: List<NameDifficultyData>,
    val correctAnswersCountPerSkill: List<NameValueData>,
    val wrongAnswersCountPerSkill: List<NameValueData>
) : TestStatistics


data class NameValueData(
    val name: String,
    val value: Number
)

data class NameDifficultyData(
    val name: String,
    val value: QuestionDifficulty
)
