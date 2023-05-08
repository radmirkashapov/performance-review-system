package dev.rkashapov.security.auth.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.BeanIds
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@EnableGlobalAuthentication
class WebSecurityConfiguration {

    companion object {
        private const val DEFAULT_LOGIN_URL = "/api/v1/login"
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {

        http
            .cors().disable()
            .authorizeHttpRequests()
            .requestMatchers("/error").permitAll()
            .requestMatchers(
                "/swagger-ui/**", "/swagger-ui.html",
                "/v3/api-docs", "/v3/api-docs/**",
                "/v3/api-docs.yaml", "/v3/api-docs.yaml/**"
            ).permitAll()
            .anyRequest().authenticated()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        return http.build()
    }

    @Bean(name = [BeanIds.AUTHENTICATION_MANAGER])
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }


}
