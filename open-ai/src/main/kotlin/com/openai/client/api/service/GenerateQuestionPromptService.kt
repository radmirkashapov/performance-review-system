package com.openai.client.api.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.openai.client.api.model.Prompt
import com.openai.client.api.model.PromptWithQuestionExamples
import com.openai.client.api.model.SimplePrompt
import dev.rkashapov.base.logging.MdcKey.USER_ID
import dev.rkashapov.base.logging.withLoggingContext
import dev.rkashapov.prs.testing.api.model.QuestionDifficulty
import dev.rkashapov.prs.testing.api.model.TestCheckListQuestionOption
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import java.util.*

@Service
class GenerateQuestionPromptService(
    private val objectMapper: ObjectMapper
) : KLogging() {

    @Value("\${prompts.question-prompts-path:classpath*:/prompts/question-prompts/*}")
    lateinit var promptsResources: Array<Resource>

    private val initPrompt by lazy {
        promptsResources
            .first { it.filename == QuestionPromptsFileNames.techPromptInit }
            .let { objectMapper.readValue<SimplePrompt>(it.contentAsByteArray) }
    }

    private val checkListAnswersContext by lazy {
        promptsResources
            .first { it.filename == QuestionPromptsFileNames.checklistAnswersContext }
            .let { objectMapper.readValue<SimplePrompt>(it.contentAsByteArray) }
    }

    private val questionDifficultiesBase by lazy {
        promptsResources
            .first { it.filename == QuestionPromptsFileNames.questionDifficultiesBase }
            .let { objectMapper.readValue<PromptWithQuestionExamples>(it.contentAsByteArray) }
    }

    private val questionGeneratePrompt by lazy {
        promptsResources
            .first { it.filename == QuestionPromptsFileNames.questionGeneratePrompt }
            .let { objectMapper.readValue<SimplePrompt>(it.contentAsByteArray) }
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun getPrompt(
        userId: UUID,
        testName: String,
        skillName: String,
        skillCheckListAnswer: Set<TestCheckListQuestionOption>,
        requestedDifficulties: Set<QuestionDifficulty>
    ): Prompt {
        withLoggingContext(USER_ID to userId) {
            val context = buildString {
                append(initPrompt.prompt)
                append(
                    checkListAnswersContext
                        .prompt
                        .replace("%userId%", userId.toString())
                        .replace("%testName%", testName)
                        .replace("%skillName%", skillName)
                        .replace("%skillCheckListAnswer%",
                            skillCheckListAnswer.joinToString(separator = ".") { it.value })
                        .replace("%skillCheckListAnswerGrade%", skillCheckListAnswer.maxBy { it.rank!!.ordinal }.rank?.name.toString())
                )
            }

            val prompt = buildString {
                append(context)
                append(
                    questionGeneratePrompt
                        .prompt
                        .replace("%difficultyRange%", requestedDifficulties.map { it.name }.joinToString(separator = ","))
                        .replace("%skillName%", skillName)
                        .replace(
                            "%globalDifficultyRage%",
                            QuestionDifficulty.entries.map { it.name }.joinToString(separator = ","))
                        .replace("%questionsExamplePrompt%", questionDifficultiesBase.prompt)
                )
            }

            logger.trace { "Generated generated question prompt: $prompt" }

            return SimplePrompt(prompt = prompt)
        }
    }
}
