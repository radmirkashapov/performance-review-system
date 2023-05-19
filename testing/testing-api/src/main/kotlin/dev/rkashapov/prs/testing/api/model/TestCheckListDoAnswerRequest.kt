package dev.rkashapov.prs.testing.api.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.util.*

data class TestCheckListDoAnswerRequest(
    @field:NotNull
    @field:JsonProperty("sessionId")
    val sessionId: UUID,

    @field:NotNull
    @field:JsonProperty("questionId")
    val questionId: UUID,

    @field:JsonProperty("answer")
    @field:NotEmpty(message = "Answers must be provided")
    val answer: Set<TestCheckListQuestionOption> = emptySet()
)
