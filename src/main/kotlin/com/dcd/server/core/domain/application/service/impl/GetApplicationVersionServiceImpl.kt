package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.GetApplicationVersionService
import com.dcd.server.core.domain.application.spi.ImageVersionPort
import org.springframework.stereotype.Service

@Service
class GetApplicationVersionServiceImpl(
    private val imageVersionPort: ImageVersionPort
) : GetApplicationVersionService {
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
            ApplicationType.SPRING_BOOT -> "amazoncorretto" to SemVer.parse("12")
            ApplicationType.NEST_JS -> "node" to SemVer.parse("17")
            ApplicationType.GIN -> "golang" to SemVer.parse("1")
            ApplicationType.MARIA_DB -> "mariadb" to SemVer.parse("10")
            ApplicationType.MYSQL -> "mysql" to SemVer.parse("8")
            ApplicationType.REDIS -> "redis" to SemVer.parse("6")
            ApplicationType.H2_DB -> "oscarfonts/h2" to SemVer.parse("0")
        }

        minVersion ?: throw IllegalArgumentException("Invalid Version")

        val result = imageVersionPort.fetchAllVersions(baseImageName)
            .mapNotNull { SemVer.parse(it) }
            .filter { it > minVersion }
            .distinct()
            .sortedDescending()
            .map { "${it.major}.${it.minor}.${it.patch}" }

        return result
    }
}