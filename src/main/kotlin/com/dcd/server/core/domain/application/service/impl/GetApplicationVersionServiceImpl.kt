package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.GetApplicationVersionService
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader

@Service
class GetApplicationVersionServiceImpl : GetApplicationVersionService {
    override fun getAvailableVersion(applicationType: ApplicationType): List<String> {
        val cmd = arrayOf("docker images ${applicationType.name.lowercase()}")
        val p = Runtime.getRuntime().exec(cmd)
        val br = BufferedReader(InputStreamReader(p.inputStream))
        var isFirst = true
        val result = mutableListOf<String>()
        while (br.readLine() != null) {
            if (isFirst) {
                isFirst = !isFirst
                continue
            }
            val split = br.readLine().split("    ")
            val tag = split[1]
            result.add(tag)
        }
        return result
    }
}