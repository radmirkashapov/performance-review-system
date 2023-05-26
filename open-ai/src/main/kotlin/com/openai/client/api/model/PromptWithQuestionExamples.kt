package com.openai.client.api.model

import dev.rkashapov.prs.testing.api.model.QuestionDifficulty

data class PromptWithQuestionExamples(
    override val prompt: String,
    val questions: Map<QuestionDifficulty, String>
) : Prompt
