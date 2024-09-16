package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.domain.application.scheduler.enums.ContainerStatus
import com.dcd.server.core.domain.application.service.GetContainerService
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader

@Service
class GetContainerServiceImpl : GetContainerService {
    override fun getContainerNameByStatus(status: ContainerStatus): List<String> {
        val cmd = arrayOf("/bin/sh", "-c", "docker ps -a --filter \"status=${status.description}\" --format \"{{.Names}}\"")
        val p = Runtime.getRuntime().exec(cmd)
        val br = BufferedReader(InputStreamReader(p.inputStream))
        val result = br.readLines()
        p.waitFor()
        p.destroy()
        return result
    }
}