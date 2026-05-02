package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.common.spi.ContainerPort
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.service.ExecContainerService
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.io.*
import java.util.Stack

@Service
class ExecContainerServiceImpl(
    private val containerPort: ContainerPort,
    private val commandPort: CommandPort
) : ExecContainerService {
    override fun execCmd(application: Application, cmd: String): List<String> {
        val result = mutableListOf<String>()
        containerPort.execute {
            executeCmd(application, "/", cmd) { output ->
                result.add(output)
            }
        }
        return result
    }

    override fun execCmd(application: Application, session: WebSocketSession, cmd: String) {
        val cmdArray = cmd.split(" ").toTypedArray()

        @Suppress("UNCHECKED_CAST")
        val dirStack = (session.attributes["workingDir"] as? Stack<String>) ?: Stack<String>()
        val workingDir = "/${dirStack.joinToString("/")}"

        if (cmd.contains("cd")) {
            val newDirList = cmdArray[1].split("/")

            newDirList.forEach { newDir ->
                updateWorkingDir(dirStack, newDir)
            }
            val updatedWorkingDir = "/${dirStack.joinToString("/")}"

            session.sendMessage(TextMessage("current dir = $updatedWorkingDir"))
            session.sendMessage(TextMessage("cmd end"))

            session.attributes["workingDir"] = dirStack
            return
        }

        session.sendMessage(TextMessage("cmd start"))

        containerPort.execute {
            executeCmd(application, workingDir, cmd) { output ->
                session.sendMessage(TextMessage(output))
            }
        }

        session.sendMessage(TextMessage("current dir = $workingDir"))
        session.sendMessage(TextMessage("cmd end"))

        session.close()
    }

    private fun updateWorkingDir(dirStack: Stack<String>, newDir: String) {
        when {
            newDir == "/" -> dirStack.removeAllElements()
            newDir == ".." -> if (dirStack.isNotEmpty()) dirStack.pop()
            newDir.startsWith("/") -> {
                dirStack.removeAllElements()
                dirStack.push(newDir)
            }
            newDir == "." -> return
            newDir.isBlank() -> return
            else -> { dirStack.push(newDir) }
        }
    }
}