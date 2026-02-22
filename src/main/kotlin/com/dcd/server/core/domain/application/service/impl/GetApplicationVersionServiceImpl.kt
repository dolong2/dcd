package com.dcd.server.core.domain.application.service.impl

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
    private val objectMapper: ObjectMapper,
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

    private data class SemVer(
        val major: Int,
        val minor: Int = 0,
        val patch: Int = 0
    ) : Comparable<SemVer> {

        override fun compareTo(other: SemVer): Int =
            compareValuesBy(this, other, SemVer::major, SemVer::minor, SemVer::patch)

        companion object {
            private val regex = Regex("""^\d+(\.\d+){0,2}$""")

            fun parse(value: String): SemVer? {
                if (!regex.matches(value)) return null

                val parts = value.split(".")
                return SemVer(
                    parts.getOrNull(0)?.toInt() ?: 0,
                    parts.getOrNull(1)?.toInt() ?: 0,
                    parts.getOrNull(2)?.toInt() ?: 0
                )
            }
        }
    }

    override fun getAvailableVersion(applicationType: ApplicationType): List<String> {
        val (baseImageName, minVersion) = when (applicationType) {
            ApplicationType.SPRING_BOOT -> "openjdk" to SemVer.parse("12")
            ApplicationType.NEST_JS -> "node" to SemVer.parse("17")
            ApplicationType.MARIA_DB -> "mariadb" to SemVer.parse("10")
            ApplicationType.MYSQL -> "mysql" to SemVer.parse("8")
            ApplicationType.REDIS -> "redis" to SemVer.parse("6")
            ApplicationType.H2_DB -> "oscarfonts/h2" to SemVer.parse("0")
        }

        minVersion ?: throw IllegalArgumentException("Invalid Version")

        val result = fetchAllTagsByDocker(baseImageName).mapNotNull { SemVer.parse(it) }
            .filter { it > minVersion }
            .distinct()
            .sortedDescending()
            .map { "${it.major}.${it.minor}.${it.patch}".trimEnd('.', '0') }

        return result
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