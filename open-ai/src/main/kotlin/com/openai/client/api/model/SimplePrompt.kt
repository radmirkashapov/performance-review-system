package com.openai.client.api.model

data class SimplePrompt(
    override val prompt: String
) : Prompt
