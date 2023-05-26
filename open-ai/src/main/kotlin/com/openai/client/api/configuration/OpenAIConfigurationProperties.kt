package com.openai.client.api.configuration

import com.aallam.openai.api.logging.LogLevel
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "open-ai")
data class OpenAIConfigurationProperties(
    val token: String,
    val loggingLevel: LogLevel,
    val modelId: String,
    val temperatureMatrix: Double = 0.2,
    val temperatureQuestions: Double = 1.2
)
