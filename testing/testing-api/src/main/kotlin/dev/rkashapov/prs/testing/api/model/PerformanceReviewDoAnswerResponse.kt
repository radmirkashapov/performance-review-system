package dev.rkashapov.prs.testing.api.model

import java.util.*

data class PerformanceReviewDoAnswerResponse(
    val nextSkill: String,
    val nextQuestion: String,
    val nextQuestionId: UUID,
    val nextQuestionAnswerOptions: List<String>,
    val nextQuestionType: QuestionType
) : DoAnswerNextQuestionResponse

data class PerformanceReviewDoAnswerFinishTestResponse(val nextQuestionDifficulty: QuestionDifficulty) : DoAnswerFinishTestResponse
