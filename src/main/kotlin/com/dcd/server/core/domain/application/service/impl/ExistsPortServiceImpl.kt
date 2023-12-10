package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.domain.application.service.ExistsPortService
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader

@Service
class ExistsPortServiceImpl
    : ExistsPortService {
    override fun existsPort(port: Int): Boolean {
        val cmd = arrayOf("/bin/sh", "-c", "lsof -i :${port}")
        val p = Runtime.getRuntime().exec(cmd)
        val br = BufferedReader(InputStreamReader(p.inputStream))
        return br.readLine() != null
    }
}