package dev.rkashapov.prs.testing.api.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.*

data class PerformanceReviewDoAnswerRequest(
    @field:NotBlank
    val skill: String,

    @field:NotNull
    val questionId: UUID,

    val answer: Map<QuestionAnswerType, List<String>> = emptyMap()
)
