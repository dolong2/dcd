package com.dcd.server.infrastructure.global.config

import com.dcd.server.infrastructure.global.filter.ExceptionFilter
import com.dcd.server.infrastructure.global.filter.JwtReqFilter
import com.dcd.server.infrastructure.global.jwt.adapter.ParseTokenAdapter
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

class FilterConfig(
    private val objectMapper: ObjectMapper,
    private val parseTokenAdapter: ParseTokenAdapter
) : SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {
    override fun configure(builder: HttpSecurity) {
        builder.addFilterBefore(ExceptionFilter(objectMapper), UsernamePasswordAuthenticationFilter::class.java)
        builder.addFilterBefore(JwtReqFilter(parseTokenAdapter), UsernamePasswordAuthenticationFilter::class.java)
    }
}