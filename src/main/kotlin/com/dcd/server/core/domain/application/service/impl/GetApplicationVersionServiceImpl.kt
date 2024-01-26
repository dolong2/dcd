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
        val cmd = arrayOf("/bin/sh", "-c", "docker images ${applicationType.name.lowercase()}")
        val p = Runtime.getRuntime().exec(cmd)
        val br = BufferedReader(InputStreamReader(p.inputStream))
        val result = mutableListOf<String>()
        while (br.readLine() != null) {
            val split = br.readLine().replace(Regex("\\s{2,}"), " ").split(" ")
            val tag = split[1]
            result.add(tag)
        }
        p.waitFor()
        p.destroy()
        return result
    }
}