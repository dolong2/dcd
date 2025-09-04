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
                it.requestMatchers(HttpMethod.POST, "/auth/email").permitAll()
                it.requestMatchers(HttpMethod.POST, "/auth/email/certificate").permitAll()
                it.requestMatchers(HttpMethod.POST, "/auth/signup").permitAll()
                it.requestMatchers(HttpMethod.POST, "/auth").permitAll()
                it.requestMatchers(HttpMethod.PATCH, "/auth").permitAll()
                it.requestMatchers(HttpMethod.DELETE, "/auth").authenticated()
                it.requestMatchers(HttpMethod.PATCH, "/auth/password").permitAll()

                //application
                it.requestMatchers(HttpMethod.POST, "/{workspaceId}/application").authenticated()
                it.requestMatchers(HttpMethod.POST, "/{workspaceId}/application/{id}/run").authenticated()
                it.requestMatchers(HttpMethod.POST, "/{workspaceId}/application/run").authenticated()
                it.requestMatchers(HttpMethod.POST, "/{workspaceId}/application/{id}/stop").authenticated()
                it.requestMatchers(HttpMethod.POST, "/{workspaceId}/application/stop").authenticated()
                it.requestMatchers(HttpMethod.POST, "/{workspaceId}/application/{id}/deploy").authenticated()
                it.requestMatchers(HttpMethod.POST, "/{workspaceId}/application/deploy").authenticated()
                it.requestMatchers(HttpMethod.GET, "/{workspaceId}/application").authenticated()
                it.requestMatchers(HttpMethod.GET, "/{workspaceId}/application/{id}").authenticated()
                it.requestMatchers(HttpMethod.DELETE, "/{workspaceId}/application/{id}").authenticated()
                it.requestMatchers(HttpMethod.PUT, "/{workspaceId}/application/{id}").authenticated()
                it.requestMatchers(HttpMethod.GET, "/{workspaceId}/application/{id}/logs").authenticated()
                it.requestMatchers(HttpMethod.POST, "/{workspaceId}/application/{id}/exec").authenticated()

                //application static
                it.requestMatchers(HttpMethod.GET, "/application/{applicationType}/version").authenticated()
                it.requestMatchers(HttpMethod.GET, "/application/exec").authenticated()
                it.requestMatchers(HttpMethod.GET, "/application/types").authenticated()

                //workspace
                it.requestMatchers(HttpMethod.POST, "/workspace").authenticated()
                it.requestMatchers(HttpMethod.GET, "/workspace").authenticated()
                it.requestMatchers(HttpMethod.GET, "/workspace/{workspaceId}").authenticated()
                it.requestMatchers(HttpMethod.DELETE, "/workspace/{workspaceId}").authenticated()
                it.requestMatchers(HttpMethod.PUT, "/workspace/{workspaceId}").authenticated()
                it.requestMatchers(HttpMethod.PUT, "/workspace/{workspaceId}/env").authenticated()
                it.requestMatchers(HttpMethod.DELETE, "/workspace/{workspaceId}/env").authenticated()

                //domain
                it.requestMatchers(HttpMethod.GET, "/{workspaceId}/domain").authenticated()
                it.requestMatchers(HttpMethod.POST, "/{workspaceId}/domain").authenticated()
                it.requestMatchers(HttpMethod.DELETE, "/{workspaceId}/domain/{domainId}").authenticated()
                it.requestMatchers(HttpMethod.POST, "/{workspaceId}/domain/{domainId}/connect").authenticated()
                it.requestMatchers(HttpMethod.POST, "/{workspaceId}/domain/{domainId}/disconnect").authenticated()

                //user
                it.requestMatchers(HttpMethod.GET, "/user/profile").authenticated()
                it.requestMatchers(HttpMethod.PATCH, "/user/password").authenticated()
                it.requestMatchers(HttpMethod.PATCH, "/user/{userId}/status").hasRole("ADMIN")
                it.requestMatchers(HttpMethod.GET, "/user").hasRole("ADMIN")

                //env
                it.requestMatchers(HttpMethod.POST, "/{workspaceId}/env").authenticated()
                it.requestMatchers(HttpMethod.DELETE, "/{workspaceId}/env/{envId}").authenticated()
                it.requestMatchers(HttpMethod.PUT, "/{workspaceId}/env/{envId}").authenticated()
                it.requestMatchers(HttpMethod.GET, "/{workspaceId}/env").authenticated()
                it.requestMatchers(HttpMethod.GET, "/{workspaceId}/env/{envId}").authenticated()

                //volume
                it.requestMatchers(HttpMethod.POST, "/{workspaceId}/volume").authenticated()
                it.requestMatchers(HttpMethod.DELETE, "/{workspaceId}/volume/{volumeId}").authenticated()

                //when url not set
                it.anyRequest().denyAll()
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