package com.openai.client.api.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.openai.client.api.model.Prompt
import com.openai.client.api.model.SimplePrompt
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service

@Service
class GenerateMatrixOfTopicConnectivityPromptService(
    private val objectMapper: ObjectMapper
) : KLogging() {

    @Value("\${prompts.matrix-prompts-path:classpath:/prompts/matrix-prompts/matrix-of-topic-connectivity-prompt.json}")
    lateinit var promptResource: Resource

    private val matrixPrompt by lazy {
        objectMapper.readValue<SimplePrompt>(promptResource.contentAsByteArray)
    }

    fun getPrompt(testName: String, sortedSkills: List<String>): Prompt {
        val prompt = buildString {
            append(
                matrixPrompt
                    .prompt
                    .replace("%themeName%", testName)
                    .replace("%topics%", sortedSkills.joinToString(separator = " "))
            )
        }

        logger.trace { "Generated generated matrix prompt: $prompt" }

        return SimplePrompt(prompt = prompt)
    }
}
