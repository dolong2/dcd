package com.dcd.server.core.common.command.adapter

import com.dcd.server.core.common.command.CommandPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

@Component
class CommandAdapter : CommandPort {
    private val log = LoggerFactory.getLogger(this::class.simpleName)

    override fun executeShellCommand(cmd: String): Int {
        val shellScriptCmd = arrayOf("/bin/sh", "-c", cmd)
        val p = Runtime.getRuntime().exec(shellScriptCmd)

        val br = BufferedReader(InputStreamReader(p.inputStream))
        br.readLines().forEach {
            log.info(it)
        }

        p.waitFor()
        val exitValue = p.exitValue()
        p.destroy()

        return exitValue
    }

    override fun executeShellCommandWithResult(cmd: String): List<String> {
        val shellScriptCmd = arrayOf("/bin/sh", "-c", cmd)
        val p = Runtime.getRuntime().exec(shellScriptCmd)
        val br = BufferedReader(InputStreamReader(p.inputStream))

        return try {
            val result = br.readLines()
            p.waitFor()
            log.info(result.joinToString("\n"))
            result
        } catch (ex: IOException) {
            log.error("명령어 실행 중 IO 오류 발생: ${ex.message}")
            emptyList()
        } catch (ex: InterruptedException) {
            log.error("명령어 실행이 중단됨: ${ex.message}")
            emptyList()
        } finally {
            p.destroy()
        }
    }

}