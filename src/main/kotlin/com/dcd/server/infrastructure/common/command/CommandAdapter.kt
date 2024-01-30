package com.dcd.server.infrastructure.common.command

import com.dcd.server.core.common.command.CommandPort
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader

@Component
class CommandAdapter : CommandPort {
    override fun executeShellCommand(cmd: String) {
        val cmd = arrayOf("/bin/sh", "-c", cmd)
        val p = Runtime.getRuntime().exec(cmd)
        val br = BufferedReader(InputStreamReader(p.inputStream))
        br.readLines().forEach {
            println("$it")
        }
        p.waitFor()
        println("p.exitValue() = ${p.exitValue()}")
        p.destroy()
    }

}