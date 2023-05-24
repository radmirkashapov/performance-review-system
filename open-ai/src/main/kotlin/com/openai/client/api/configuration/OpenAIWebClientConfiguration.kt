package com.openai.client.api.configuration

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.RetryStrategy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.time.Duration.Companion.seconds

@Configuration
class OpenAIWebClientConfiguration(
    private val configurationProperties: OpenAIConfigurationProperties
) {

    @Bean
    fun openAIWebClient() = OpenAI(
        OpenAIConfig(
            token = configurationProperties.token,
            timeout = Timeout(socket = 60.seconds),
            retry = RetryStrategy(maxRetries = 0) // because of 500 http status
        )
    )
}
