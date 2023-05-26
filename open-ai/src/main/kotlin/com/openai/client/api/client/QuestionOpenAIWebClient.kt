package com.openai.client.api.client

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.client.OpenAI
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.openai.client.api.configuration.OpenAIConfigurationProperties
import com.openai.client.api.exception.QuestionsNotFound
import com.openai.client.api.model.GeneratedQuestionsModel
import com.openai.client.api.service.GenerateQuestionPromptService
import dev.rkashapov.base.logging.MdcKey.USER_ID
import dev.rkashapov.base.logging.withCoroutineLoggingContext
import dev.rkashapov.prs.testing.api.model.QuestionDifficulty
import dev.rkashapov.prs.testing.api.model.TestCheckListQuestionOption
import org.springframework.stereotype.Component
import java.util.*

@Component
class QuestionOpenAIWebClient(
    private val configurationProperties: OpenAIConfigurationProperties,
    private val openAIWebClient: OpenAI,
    private val objectMapper: ObjectMapper,
    private val generateQuestionPromptService: GenerateQuestionPromptService
) : AbstractOpenAIWebClient(
    configurationProperties = configurationProperties,
    openAIWebClient = openAIWebClient
) {

    private val jsonParser = Regex.escape("(?:\\{(?:[^\\{\\}]|(?R))*\\})").toRegex()


    @OptIn(BetaOpenAI::class)
    suspend fun getQuestionsWithDifficultyRange(
        userId: UUID,
        testName: String,
        skillName: String,
        skillCheckListAnswer: Set<TestCheckListQuestionOption>,
        requestedDifficulties: Set<QuestionDifficulty>
    ): GeneratedQuestionsModel {
        return withCoroutineLoggingContext(USER_ID to userId) {

            logger.info { "Received request to generate questions to GPT model" }

            val prompt = generateQuestionPromptService
                .getPrompt(
                    userId = userId,
                    testName = testName,
                    skillName = skillName,
                    skillCheckListAnswer = skillCheckListAnswer,
                    requestedDifficulties = requestedDifficulties
                )

            val completion = chatCompletion(prompt = prompt, temperature = configurationProperties.temperatureQuestions)

            logger.debug { "Received completion result from GPT: $completion" }

            completion
                .choices
                .first()
                .let { choice -> objectMapper.readValue(choice.message?.content?.trim() ?: throw QuestionsNotFound()) }

        }


    }

}
