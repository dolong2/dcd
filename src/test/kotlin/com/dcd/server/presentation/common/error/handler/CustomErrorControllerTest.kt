package com.dcd.server.presentation.common.error.handler

import com.dcd.server.core.common.error.ErrorCode
import com.dcd.server.presentation.common.error.response.ErrorResponse
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import jakarta.servlet.RequestDispatcher
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest

class CustomErrorControllerTest : BehaviorSpec({
    val customErrorController = CustomErrorController()

    given("sendError로 포워딩된 요청이 주어지고") {
        `when`("원래 status가 404이면") {
            val request = MockHttpServletRequest()
            request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 404)
            val result = customErrorController.handleError(request)

            then("status를 유지한 ErrorResponse를 응답해야함") {
                result.statusCode shouldBe HttpStatus.NOT_FOUND
                result.body shouldBe ErrorResponse(404, ErrorCode.NOT_FOUND.msg)
            }
        }

        `when`("원래 status가 매핑되지 않은 4xx이면") {
            val request = MockHttpServletRequest()
            request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 413)
            val result = customErrorController.handleError(request)

            then("status는 유지하고 BAD_REQUEST 메시지를 응답해야함") {
                result.statusCode shouldBe HttpStatus.PAYLOAD_TOO_LARGE
                result.body shouldBe ErrorResponse(413, ErrorCode.BAD_REQUEST.msg)
            }
        }

        `when`("status 정보가 없으면") {
            val result = customErrorController.handleError(MockHttpServletRequest())

            then("500 INTERNAL_ERROR를 응답해야함") {
                result.statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR
                result.body shouldBe ErrorResponse(ErrorCode.INTERNAL_ERROR)
            }
        }
    }
})
