package com.dcd.server.infrastructure.global.config

import com.dcd.server.infrastructure.global.jwt.adapter.ParseTokenAdapter
import com.dcd.server.infrastructure.global.security.CustomAccessDeniedHandler
import com.dcd.server.infrastructure.global.security.CustomAuthenticationEntryPoint
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.cors.CorsUtils

@Configuration
class SecurityConfig(
    private val parseTokenAdapter: ParseTokenAdapter,
    private val objectMapper: ObjectMapper,
    private val customAccessDeniedHandler: CustomAccessDeniedHandler,
) {
    @Bean
    protected fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors {  }
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }

        http
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

        http
            .authorizeHttpRequests {
                it.requestMatchers(RequestMatcher { request ->
                    CorsUtils.isPreFlightRequest(request)
                }).permitAll()

                //auth
                .requestMatchers(HttpMethod.POST, "/auth/email").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/email/certificate").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/signup").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/auth").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/auth").authenticated()

                //application
                .requestMatchers(HttpMethod.POST, "/application/{workspaceId}").authenticated()
                .requestMatchers(HttpMethod.POST, "/application/{id}/run").authenticated()
                .requestMatchers(HttpMethod.POST, "/application/{id}/run/spring").authenticated()
                .requestMatchers(HttpMethod.GET, "/application").authenticated()
                .requestMatchers(HttpMethod.GET, "/application/{id}").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/application/{id}/env").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/application/{id}/env").authenticated()
                .requestMatchers(HttpMethod.POST, "/application/{id}/stop").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/application/{id}").authenticated()

                //workspace
                .requestMatchers(HttpMethod.POST, "/workspace").authenticated()

                //when url not set
                .anyRequest().denyAll()
            }

        http
            .apply(FilterConfig(objectMapper, parseTokenAdapter))

        http
            .exceptionHandling {
                it.authenticationEntryPoint(CustomAuthenticationEntryPoint(objectMapper))
                it.accessDeniedHandler(customAccessDeniedHandler)
            }

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder =
        BCryptPasswordEncoder()
}