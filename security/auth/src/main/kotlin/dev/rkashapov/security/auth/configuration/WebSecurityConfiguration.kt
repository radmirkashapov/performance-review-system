package dev.rkashapov.security.auth.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import dev.rkashapov.security.auth.filter.JWTAuthorizationFilter
import dev.rkashapov.security.auth.filter.YandexOAuthCodeCallbackAuthenticationFilter
import dev.rkashapov.security.auth.handler.YandexOAuthCodeCallbackAuthenticationFailureHandler
import dev.rkashapov.security.auth.handler.YandexOAuthCodeCallbackAuthenticationSuccessHandler
import dev.rkashapov.security.core.service.AuthJWTService
import dev.rkashapov.security.oauth.service.YandexOAuthService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.BeanIds
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@EnableGlobalAuthentication
class WebSecurityConfiguration(
    private val userDetailsService: UserDetailsService,
    private val objectMapper: ObjectMapper,
    private val yandexOAuthService: YandexOAuthService,
    private val authJWTService: AuthJWTService,
    private val authenticationConfiguration: AuthenticationConfiguration,
    private val yandexOAuthCodeCallbackAuthenticationSuccessHandler: YandexOAuthCodeCallbackAuthenticationSuccessHandler,
    private val yandexOAuthCodeCallbackAuthenticationFailureHandler: YandexOAuthCodeCallbackAuthenticationFailureHandler
) {

    companion object {
        const val DEFAULT_LOGIN_URL = "/api/v1/oauth/yandex/code-login-callback"
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {

        val yandexOAuthCodeCallbackAuthenticationFilter = YandexOAuthCodeCallbackAuthenticationFilter(
            objectMapper,
            yandexOAuthService
        ).apply {
            setAuthenticationManager(authenticationManager(authenticationConfiguration))
            setAuthenticationSuccessHandler(yandexOAuthCodeCallbackAuthenticationSuccessHandler)
            setAuthenticationFailureHandler(yandexOAuthCodeCallbackAuthenticationFailureHandler)
        }


        http
            .cors()
            .and()
            .csrf()
            .disable()
            .authorizeHttpRequests()
            .requestMatchers(HttpMethod.POST, DEFAULT_LOGIN_URL).permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/oauth/link").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/refresh-token").permitAll()
            .requestMatchers("/error").permitAll()
            .requestMatchers(
                "/swagger-ui/**", "/swagger-ui.html",
                "/v3/api-docs", "/v3/api-docs/**",
                "/v3/api-docs.yaml", "/v3/api-docs.yaml/**"
            ).permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(
                yandexOAuthCodeCallbackAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterAt(
                JWTAuthorizationFilter(userDetailsService, authJWTService),
                LogoutFilter::class.java,
            )

            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        return http.build()
    }

    @Bean(name = [BeanIds.AUTHENTICATION_MANAGER])
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }


}
