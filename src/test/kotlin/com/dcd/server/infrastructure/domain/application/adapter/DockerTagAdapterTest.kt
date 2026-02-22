package com.dcd.server.infrastructure.domain.application.adapter

import com.dcd.server.infrastructure.domain.application.exception.DockerHubRateLimitExceededException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import java.net.http.HttpClient

class DockerTagAdapterTest : BehaviorSpec({
    val mockWebServer = MockWebServer()
    val objectMapper = ObjectMapper().registerKotlinModule()
    val client = HttpClient.newHttpClient()

    beforeSpec {
        mockWebServer.start()
    }

    afterSpec {
        mockWebServer.shutdown()
    }

    given("Docker Hub API가 정상 응답을 반환할 때") {
        val body = """
            {
              "next": null,
              "results": [
                {"name": "1.0.0"},
                {"name": "2.0"},
                {"name": "latest"}
              ]
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(body)
        )

        val adapter = DockerTagAdapter(
            objectMapper = objectMapper,
            client = client,
            registryUrl = mockWebServer.url("/").toString().removeSuffix("/"),
            token = "test",
        )

        println(mockWebServer.url("/").toString().removeSuffix("/"))

        `when`("태그를 조회하면") {
            val url = mockWebServer.url("/v2/repositories/library/test/tags?page_size=100")
            val result = adapter.fetchAllVersions("test")

            then("태그 목록이 반환된다") {
                result shouldBe listOf("1.0.0", "2.0", "latest")
            }
        }
    }

    given("429 응답을 받을 때") {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(429)
        )

        val adapter = DockerTagAdapter(
            objectMapper = objectMapper,
            token = "test",
            registryUrl = mockWebServer.url("/").toString().removeSuffix("/"),
            client = client
        )

        `when`("태그 조회를 하면") {

            then("Rate limit 예외가 발생한다") {
                shouldThrow<DockerHubRateLimitExceededException> {
                    adapter.fetchAllVersions("test")
                }
            }
        }
    }

    given("429 응답이 아닌 400번대 응답을 받을때") {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(401)
        )

        val adapter = DockerTagAdapter(
            objectMapper = objectMapper,
            token = "test",
            registryUrl = mockWebServer.url("/").toString().removeSuffix("/"),
            client = client
        )

        `when`("태그 조회를 하면") {

            then("RuntimeException이 발생한다.") {
                shouldThrow<RuntimeException> {
                    adapter.fetchAllVersions("test")
                }
            }
        }
    }
})