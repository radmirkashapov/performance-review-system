package dev.rkashapov.security.core.configuration

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY
import com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
class BCryptConfiguration {

    @Bean
    fun bCryptPasswordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun prsJackson2ObjectMapperCustomizer() = Jackson2ObjectMapperBuilderCustomizer { builder ->
        builder.serializationInclusion(JsonInclude.Include.NON_NULL)

        builder.featuresToDisable(WRITE_DATES_AS_TIMESTAMPS)

        builder.featuresToEnable(SORT_PROPERTIES_ALPHABETICALLY)
    }


    @Bean
    fun objectMapper() = jacksonMapperBuilder()
        .findAndAddModules()
        .apply {

        }
        .build()

}

