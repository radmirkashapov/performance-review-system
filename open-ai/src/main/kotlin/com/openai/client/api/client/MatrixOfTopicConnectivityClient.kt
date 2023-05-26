package com.openai.client.api.client

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.client.OpenAI
import com.openai.client.api.configuration.OpenAIConfigurationProperties
import com.openai.client.api.service.GenerateMatrixOfTopicConnectivityPromptService
import org.springframework.stereotype.Component

@Component
class MatrixOfTopicConnectivityClient(
    private val configurationProperties: OpenAIConfigurationProperties,
    private val openAIWebClient: OpenAI,
    private val generateMatrixOfTopicConnectivityPromptService: GenerateMatrixOfTopicConnectivityPromptService
) : AbstractOpenAIWebClient(
    configurationProperties = configurationProperties,
    openAIWebClient = openAIWebClient
) {

    private val matrixRegex = "\\d.+".toRegex()

    @OptIn(BetaOpenAI::class)
    suspend fun getMatrix(
        testName: String,
        sortedSkills: List<String>
    ): List<List<Double>> {
        val prompt =
            generateMatrixOfTopicConnectivityPromptService.getPrompt(testName = testName, sortedSkills = sortedSkills)

        val completionResult = chatCompletion(prompt = prompt, temperature = configurationProperties.temperatureMatrix)
            .choices

        require(completionResult.isNotEmpty())

        logger.debug { "Received completion result from GPT: $completionResult" }

        return completionResult
            .first()
            .message!!
            .content
            .split("\n")
            .map { row -> row.split(" ").map { element -> element.toDouble() } }
    }

}
