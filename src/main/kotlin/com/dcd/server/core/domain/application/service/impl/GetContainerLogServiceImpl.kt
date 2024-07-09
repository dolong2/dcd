package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.service.GetContainerLogService
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader

@Service
class GetContainerLogServiceImpl : GetContainerLogService {
    override fun getLogs(application: Application): List<String> {
        val cmd = arrayOf("/bin/sh", "-c", "docker logs ${application.name.lowercase()}")
        val p = Runtime.getRuntime().exec(cmd)
        val br = BufferedReader(InputStreamReader(p.inputStream))
        val result = br.readLines()
        p.waitFor()
        p.destroy()
        return result
    }
}