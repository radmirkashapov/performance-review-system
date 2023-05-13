package dev.rkashapov.user.configuration

import io.swagger.v3.oas.models.OpenAPI
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UserOpenApiConfiguration(
    private val customizeOpenAPI: OpenAPI
) {
    @Bean
    fun userApi() = requireNotNull(
        GroupedOpenApi.builder()
            .group("User profile")
            .pathsToMatch(
                "/api/v1/profile/**"
            )
            .addOpenApiCustomizer {
                customizeOpenAPI
            }
            .build()
    )

}
