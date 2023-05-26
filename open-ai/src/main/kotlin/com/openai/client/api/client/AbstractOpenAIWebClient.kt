package com.openai.client.api.client

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.openai.client.api.configuration.OpenAIConfigurationProperties
import com.openai.client.api.model.Prompt
import mu.KLogging

abstract class AbstractOpenAIWebClient(
    private val openAIWebClient: OpenAI,
    private val configurationProperties: OpenAIConfigurationProperties
) : KLogging() {

    @OptIn(BetaOpenAI::class)
    suspend fun chatCompletion(prompt: Prompt, temperature: Double): ChatCompletion {
        return openAIWebClient.chatCompletion(
            request = ChatCompletionRequest(
                model = ModelId(configurationProperties.modelId),
                temperature = temperature,
                messages = listOf(ChatMessage(role = ChatRole.System, name = "prs", content = prompt.prompt))
            )
        )
    }

}
