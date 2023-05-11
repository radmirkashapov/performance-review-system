package dev.rkashapov.security.auth.configuration

import io.swagger.v3.oas.models.OpenAPI
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AuthenticationOpenApiConfiguration(
    private val customizeOpenAPI: OpenAPI
) {
    @Bean
    fun authenticationApi() = requireNotNull(
        GroupedOpenApi.builder()
            .group("Authentication")
            .pathsToMatch(
                "/api/v1/oauth/yandex/code-login-callback",
                "/api/v1/refresh-token"
            )
            .addOpenApiCustomizer {
                customizeOpenAPI
            }
            .build()
    )

}
