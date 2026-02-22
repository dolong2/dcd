package com.dcd.server.infrastructure.domain.application.adapter

import com.dcd.server.core.domain.application.spi.ImageVersionPort
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

@Component
class DockerTagAdapter(
    private val objectMapper: ObjectMapper,
    @Value("\${docker.token}")
    private val token: String,
    @Value("\${docker.url}")
    private val registryUrl: String,
    private val client: HttpClient
) : ImageVersionPort {

    private class TagResponse(
        val next: String?,
        @field:JsonProperty("results")
        private val rawResults: List<RawTagResponse>,
    ) {
        val tags: List<String> = rawResults.map { it.name }
        companion object {
            private class RawTagResponse(
                val name: String,
            )
        }
    }

    override fun fetchAllVersions(imageName: String): List<String> {
        val imagePrefix = if (imageName.contains("/")) "" else "library/"

        var next: String? = "${registryUrl}/v2/repositories/$imagePrefix$imageName/tags?page_size=100"

        val result = mutableListOf<String>()

        while (next != null) {

            val request = HttpRequest.newBuilder()
                .uri(URI.create(next))
                .timeout(Duration.ofSeconds(5))
                .header("Authorization", "$token")
                .GET()
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            if (response.statusCode() == 429) {
                throw RuntimeException("Docker Hub rate limit exceeded")
            }

            if (response.statusCode() !in 200..299) {
                throw RuntimeException("Docker Hub error: ${response.statusCode()}")
            }

            val body = response.body()
            val tagResponse = objectMapper.readValue(body, TagResponse::class.java)

            result += tagResponse.tags
            next = tagResponse.next
        }

        return result
    }
}