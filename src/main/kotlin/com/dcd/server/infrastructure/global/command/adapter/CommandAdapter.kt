package com.dcd.server.infrastructure.global.command.adapter

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.common.command.dto.CommandResult
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

@Component
class CommandAdapter : CommandPort {
    private val log = LoggerFactory.getLogger(this::class.simpleName)

    override fun executeShellCommand(cmd: String): CommandResult {
        val shellScriptCmd = arrayOf("/bin/sh", "-c", cmd)
        val p = Runtime.getRuntime().exec(shellScriptCmd)
        val stdout = BufferedReader(InputStreamReader(p.inputStream))
        val stderr = BufferedReader(InputStreamReader(p.errorStream))

        try {
            val result = stdout.readLines() + stderr.readLines()
            p.waitFor()
            val exitValue = p.exitValue()
            result.forEach {
                log.debug(it)
            }
            return CommandResult(exitValue, result)
        } catch (ex: IOException) {
            log.error("명령어 실행 중 IO 오류 발생: ${ex.message}")
            return CommandResult(1, listOf("명령어 실행 중 IO 오류 발생: ${ex.message}"))
        } catch (ex: InterruptedException) {
            log.error("명령어 실행이 중단됨: ${ex.message}")
            return CommandResult(1, listOf("명령어 실행이 중단됨: ${ex.message}"))
        } finally {
            stderr.close()
            stdout.close()
            p.destroy()
        }
    }
}