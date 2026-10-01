package com.dcd.server.infrastructure.global.security

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpMethod
import org.springframework.http.server.RequestPath
import org.springframework.stereotype.Component
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.servlet.handler.HandlerMappingIntrospector
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler
import org.springframework.web.util.ServletRequestPathUtils

/**
 * 시큐리티 단계에서 차단된 요청이 실제로 매핑된 핸들러가 있는 요청인지 확인
 * - 존재하지 않는 경로는 404, 경로는 있지만 메서드가 다르면 405로 응답하기 위해 사용
 */
@Component
class RequestMappingInspector(
    private val handlerMappingIntrospector: HandlerMappingIntrospector
) {
    sealed interface Result {
        object Matched : Result
        object NotFound : Result
        data class MethodNotAllowed(val supportedMethods: Set<HttpMethod>) : Result
    }

    fun inspect(request: HttpServletRequest): Result {
        val previousPath = request.getAttribute(ServletRequestPathUtils.PATH_ATTRIBUTE) as? RequestPath
        ServletRequestPathUtils.parseAndCache(request)
        try {
            for (handlerMapping in handlerMappingIntrospector.handlerMappings) {
                val chain = try {
                    handlerMapping.getHandler(request)
                } catch (ex: HttpRequestMethodNotSupportedException) {
                    return Result.MethodNotAllowed(ex.supportedHttpMethods.orEmpty())
                } catch (ex: Exception) {
                    // 경로와 메서드는 일치하지만 미디어 타입, 파라미터 조건이 맞지 않는 경우
                    return Result.Matched
                } ?: continue

                // 정적 리소스 핸들러는 모든 경로(/**)에 매핑되므로 API 존재 여부 판단에서 제외
                if (chain.handler is ResourceHttpRequestHandler)
                    continue

                return Result.Matched
            }
            return Result.NotFound
        } finally {
            ServletRequestPathUtils.setParsedRequestPath(previousPath, request)
        }
    }
}
