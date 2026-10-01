package com.dcd.server.infrastructure.global.socket.interceptor

import com.dcd.server.core.common.error.ErrorCode
import com.dcd.server.infrastructure.global.jwt.adapter.ParseTokenAdapter
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.http.server.ServletServerHttpResponse
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.socket.WebSocketHandler

class WebSocketInterceptorTest : BehaviorSpec({
    val parseTokenAdapter = mockk<ParseTokenAdapter>()
    val objectMapper = ObjectMapper()
    val webSocketInterceptor = WebSocketInterceptor(parseTokenAdapter, objectMapper)
    val wsHandler = mockk<WebSocketHandler>()

    fun handshake(query: String?, authorization: String?): Triple<Boolean, MockHttpServletResponse, MutableMap<String, Any>> {
        val servletRequest = MockHttpServletRequest("GET", "/application/exec")
        servletRequest.queryString = query
        authorization?.let { servletRequest.addHeader("Authorization", it) }
        val servletResponse = MockHttpServletResponse()
        val response = ServletServerHttpResponse(servletResponse)
        val attributes = mutableMapOf<String, Any>()

        val result = webSocketInterceptor.beforeHandshake(ServletServerHttpRequest(servletRequest), response, wsHandler, attributes)
        response.flush()
        return Triple(result, servletResponse, attributes)
    }

    given("핸드셰이크 요청이 주어지고") {
        every { parseTokenAdapter.parseToken("Bearer token") } returns "token"
        every { parseTokenAdapter.parseToken("token") } returns null

        `when`("토큰과 애플리케이션 아이디가 올바르면") {
            val (result, _, attributes) = handshake("applicationId=testId", "Bearer token")

            then("핸드셰이크를 진행하고 속성에 정보를 담아야함") {
                result shouldBe true
                attributes["accessToken"] shouldBe "token"
                attributes["applicationId"] shouldBe "testId"
            }
        }

        `when`("애플리케이션 아이디가 없으면") {
            val (result, response, _) = handshake(null, "Bearer token")

            then("400 응답과 함께 핸드셰이크를 중단해야함") {
                result shouldBe false
                response.status shouldBe 400
                objectMapper.readValue(response.contentAsByteArray, Map::class.java)["status"] shouldBe 400
            }
        }

        `when`("애플리케이션 아이디 값이 비어있으면") {
            val (result, response, _) = handshake("applicationId", "Bearer token")

            then("400 응답과 함께 핸드셰이크를 중단해야함") {
                result shouldBe false
                response.status shouldBe 400
            }
        }

        `when`("토큰 접두사가 올바르지 않으면") {
            val (result, response, _) = handshake("applicationId=testId", "token")

            then("401 NOT_VALID_TOKEN 응답과 함께 핸드셰이크를 중단해야함") {
                result shouldBe false
                response.status shouldBe ErrorCode.NOT_VALID_TOKEN.code
                objectMapper.readValue(response.contentAsByteArray, Map::class.java)["message"] shouldBe ErrorCode.NOT_VALID_TOKEN.msg
            }
        }
    }
})
