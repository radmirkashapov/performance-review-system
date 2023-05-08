package dev.rkashapov.security.core.configuration

import dev.rkashapov.base.security.SecurityConstants.BEARER_TOKEN_PREFIX
import io.netty.handler.codec.http.HttpScheme.HTTP
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders

@Configuration
class OpenApiJWTConfiguration {

    @Bean
    fun customizeOpenAPI(): OpenAPI {
        val securityScheme = SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme(BEARER_TOKEN_PREFIX)
            .bearerFormat("JWT")
            .`in`(SecurityScheme.In.HEADER)

        return OpenAPI()
            .components(
                Components()
                    .addSecuritySchemes(
                        HttpHeaders.AUTHORIZATION,
                        securityScheme
                    )
            )
    }

}

