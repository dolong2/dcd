package com.dcd.server.core.common.command.adapter

import com.dcd.server.core.common.command.CommandPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader

@Component
class CommandAdapter : CommandPort {
    private val log = LoggerFactory.getLogger(this::class.simpleName)

    override fun executeShellCommand(cmd: String): Int {
        val cmd = arrayOf("/bin/sh", "-c", cmd)
        val p = Runtime.getRuntime().exec(cmd)
        val br = BufferedReader(InputStreamReader(p.inputStream))
        br.readLines().forEach {
            log.info("$it")
        }
        p.waitFor()
        val exitValue = p.exitValue()
        p.destroy()
        return exitValue
    }

    override fun executeShellCommandWithResult(cmd: String): List<String> {
        val cmd = arrayOf("/bin/sh", "-c", cmd)
        val p = Runtime.getRuntime().exec(cmd)
        val br = BufferedReader(InputStreamReader(p.inputStream))
        p.waitFor()
        p.destroy()
        return br.readLines()
    }

}