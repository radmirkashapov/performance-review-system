package dev.rkashapov.security.core.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
class BCryptConfiguration {

    @Bean
    fun bCryptPasswordEncoder() = BCryptPasswordEncoder()

}

