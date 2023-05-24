package com.openai.client.api.model

sealed interface Prompt {
    val prompt: String
}
