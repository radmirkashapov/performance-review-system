package com.openai.client.api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import dev.rkashapov.prs.testing.api.model.QuestionDifficulty
import dev.rkashapov.prs.testing.api.model.QuestionType

data class GeneratedQuestionsModel(
    @JsonProperty("questions")
    val questions: List<GeneratedQuestion> = emptyList()
) {
    data class GeneratedQuestion(
        @JsonProperty("question")
        val question: String,

        @JsonProperty("difficulty")
        val difficulty: QuestionDifficulty,

        @JsonProperty("type")
        val type: QuestionType,

        @JsonProperty("answer_options")
        val answerOptionsAsString: String,

        @JsonProperty("correct_answers")
        val correctAnswersAsString: String
    ) {
        @JsonIgnore
        val answerOptions: List<String> = answerOptionsAsString.split("!").filter { it.isNotEmpty() }.map { it.trim() }

        @JsonIgnore
        val correctAnswers: List<String> = correctAnswersAsString
            .split("!")
            .filter { it.isNotEmpty() }
            .mapNotNull { answer -> answerOptions.find { option -> option.startsWith(answer.trim()) } }
    }
}
