package com.dcd.server.infrastructure.global.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.web.filter.OncePerRequestFilter

class RequestInfoLogFilter : OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(this::class.simpleName)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        log.info("request uri: ${request.requestURI}")
        log.info("request method: ${request.method}")
        log.info("request remote addr: ${request.remoteAddr}")
        log.info("request port: ${request.remotePort}")

        filterChain.doFilter(request, response)

        log.info("response status: ${response.status}")
    }
}