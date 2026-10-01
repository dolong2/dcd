package com.dcd.server.presentation.common.error

import com.dcd.server.core.common.error.ErrorCode
import com.dcd.server.core.domain.auth.spi.GenerateTokenPort
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import org.springframework.test.context.ActiveProfiles
import java.net.Socket
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/**
 * 실제 서블릿 컨테이너 위에서 필터, 시큐리티, 예외 핸들러를 모두 거친 에러 응답의 status와 스키마를 검증
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ErrorResponseStatusTest(
    private val environment: Environment,
    private val generateTokenPort: GenerateTokenPort,
    private val objectMapper: ObjectMapper
) : BehaviorSpec({
    val client = HttpClient.newHttpClient()
    val port by lazy { environment.getProperty("local.server.port")!!.toInt() }
    val accessToken by lazy { generateTokenPort.generateToken("1e1973eb-3fb9-47ac-9342-c16cd63ffc6f").accessToken }

    fun request(
        method: String,
        path: String,
        body: String? = null,
        contentType: String = "application/json",
        authorization: String? = null
    ): HttpResponse<String> {
        val builder = HttpRequest.newBuilder(URI.create("http://localhost:$port$path"))
            .method(method, body?.let { HttpRequest.BodyPublishers.ofString(it) } ?: HttpRequest.BodyPublishers.noBody())
            .header("Content-Type", contentType)
        authorization?.let { builder.header("Authorization", it) }
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString())
    }

    fun HttpResponse<String>.shouldBeError(status: Int, message: String) {
        statusCode() shouldBe status
        val body = objectMapper.readValue(body(), Map::class.java)
        body["status"] shouldBe status
        body["message"] shouldBe message
    }

    given("인증 정보 없이") {
        `when`("요청 바디의 JSON 형식이 올바르지 않으면") {
            val response = request("POST", "/auth", """{"email":""")

            then("400 BAD_REQUEST를 응답해야함") {
                response.shouldBeError(400, ErrorCode.BAD_REQUEST.msg)
            }
        }

        `when`("요청 바디 검증에 실패하면") {
            val response = request("POST", "/auth", """{"email":"","password":""}""")

            then("400 BAD_REQUEST를 응답해야함") {
                response.shouldBeError(400, ErrorCode.BAD_REQUEST.msg)
            }
        }

        `when`("필수 헤더가 없으면") {
            val response = request("PATCH", "/auth")

            then("400 BAD_REQUEST를 응답해야함") {
                response.shouldBeError(400, ErrorCode.BAD_REQUEST.msg)
            }
        }

        `when`("지원하지 않는 Content-Type으로 요청하면") {
            val response = request("POST", "/auth", "text", contentType = "text/plain")

            then("415 UNSUPPORTED_MEDIA_TYPE을 응답해야함") {
                response.shouldBeError(415, ErrorCode.UNSUPPORTED_MEDIA_TYPE.msg)
            }
        }

        `when`("인증이 필요한 API를 요청하면") {
            val response = request("GET", "/workspace")

            then("401 UNAUTHORIZED를 응답해야함") {
                response.shouldBeError(401, ErrorCode.UNAUTHORIZED.msg)
            }
        }

        `when`("유효하지 않은 토큰으로 요청하면") {
            val response = request("GET", "/workspace", authorization = "Bearer invalid.token.value")

            then("401 NOT_VALID_TOKEN을 응답해야함") {
                response.shouldBeError(401, ErrorCode.NOT_VALID_TOKEN.msg)
            }
        }

        `when`("존재하지 않는 경로를 요청하면") {
            val response = request("GET", "/not-exists-path")

            then("404 NOT_FOUND를 응답해야함") {
                response.shouldBeError(404, ErrorCode.NOT_FOUND.msg)
            }
        }

        `when`("경로는 있지만 지원하지 않는 메서드로 요청하면") {
            val response = request("PUT", "/auth")

            then("405 METHOD_NOT_ALLOWED와 Allow 헤더를 응답해야함") {
                response.shouldBeError(405, ErrorCode.METHOD_NOT_ALLOWED.msg)
                response.headers().firstValue("Allow").orElse("") shouldContain "POST"
            }
        }
    }

    given("인증된 사용자가") {
        `when`("존재하지 않는 경로를 요청하면") {
            val response = request("GET", "/not-exists-path", authorization = "Bearer $accessToken")

            then("404 NOT_FOUND를 응답해야함") {
                response.shouldBeError(404, ErrorCode.NOT_FOUND.msg)
            }
        }

        `when`("경로는 있지만 지원하지 않는 메서드로 요청하면") {
            val response = request("DELETE", "/workspace", authorization = "Bearer $accessToken")

            then("405 METHOD_NOT_ALLOWED와 Allow 헤더를 응답해야함") {
                response.shouldBeError(405, ErrorCode.METHOD_NOT_ALLOWED.msg)
                response.headers().firstValue("Allow").orElse("") shouldContain "GET"
            }
        }

        `when`("권한이 없는 API를 요청하면") {
            val response = request("GET", "/user?status=CREATED", authorization = "Bearer $accessToken")

            then("403 INVALID_ROLE을 응답해야함") {
                response.shouldBeError(403, ErrorCode.INVALID_ROLE.msg)
            }
        }

        `when`("필수 요청 파라미터 없이 요청하면") {
            val response = request("POST", "/d57b42f5-5cc4-440b-8dce-b4fc2e372eff/application/run", authorization = "Bearer $accessToken")

            then("400 BAD_REQUEST를 응답해야함") {
                response.shouldBeError(400, ErrorCode.BAD_REQUEST.msg)
            }
        }

        `when`("UUID 형식이 아닌 문자열 식별자로 리소스를 조회하면") {
            val response = request("GET", "/workspace/not-uuid", authorization = "Bearer $accessToken")

            then("404 WORKSPACE_NOT_FOUND를 응답해야함") {
                response.shouldBeError(404, ErrorCode.WORKSPACE_NOT_FOUND.msg)
            }
        }

        `when`("UUID 타입 경로 변수에 UUID 형식이 아닌 값으로 요청하면") {
            val response = request("GET", "/d57b42f5-5cc4-440b-8dce-b4fc2e372eff/env/not-uuid", authorization = "Bearer $accessToken")

            then("404 NOT_FOUND를 응답해야함") {
                response.shouldBeError(404, ErrorCode.NOT_FOUND.msg)
            }
        }

        `when`("애플리케이션 아이디 없이 웹소켓 핸드셰이크를 요청하면") {
            val rawResponse = Socket("localhost", port).use { socket ->
                socket.soTimeout = 5000
                socket.getOutputStream().write(
                    ("GET /application/exec HTTP/1.1\r\n" +
                        "Host: localhost\r\n" +
                        "Upgrade: websocket\r\n" +
                        "Connection: Upgrade\r\n" +
                        "Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==\r\n" +
                        "Sec-WebSocket-Version: 13\r\n" +
                        "Authorization: Bearer $accessToken\r\n\r\n").toByteArray()
                )
                // keep-alive 연결일 수 있으므로 JSON 바디를 다 읽거나 연결이 끝날 때까지만 읽음
                val input = socket.getInputStream()
                val received = StringBuilder()
                val chunk = ByteArray(1024)
                while (!received.endsWith("}")) {
                    val size = input.read(chunk)
                    if (size < 0) break
                    received.append(String(chunk, 0, size))
                }
                received.toString()
            }

            then("400 BAD_REQUEST를 응답해야함") {
                rawResponse shouldStartWith "HTTP/1.1 400"
                rawResponse shouldContain """"status":400"""
            }
        }
    }
})
