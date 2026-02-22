package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.common.file.FileContent
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.GetApplicationVersionService
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

@Service
class GetApplicationVersionServiceImpl(
    private val commandPort: CommandPort
    @Value("\${docker.token}")
    private val token: String,
) : GetApplicationVersionService {
    private val client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(3))
        .build()

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

    override fun getAvailableVersion(applicationType: ApplicationType): List<String> {
        val (baseImageName, minVersion) = when (applicationType) {
            ApplicationType.SPRING_BOOT -> "openjdk" to "12"
            ApplicationType.NEST_JS -> "node" to "17"
            ApplicationType.MARIA_DB -> "mariadb" to "10"
            ApplicationType.MYSQL -> "mysql" to "8"
            ApplicationType.REDIS -> "redis" to "6"
            ApplicationType.H2_DB -> "oscarfonts/h2" to "0"
        }
        val getVersionScript = FileContent.getImageVersionShellScriptContent(baseImageName, minVersion)
        return commandPort.executeShellCommand(getVersionScript).result
    }

    private fun fetchAllTagsByDocker(imageName: String): List<String> {
        val imagePrefix = if (imageName.contains("/")) "" else "library/"

        var next: String? = "https://registry.hub.docker.com/v2/repositories/$imagePrefix$imageName/tags?page_size=100"

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