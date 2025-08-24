package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.service.ExecContainerService
import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.model.Frame
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.io.*
import java.util.Stack

@Service
class ExecContainerServiceImpl(
    private val dockerClient: DockerClient,
    private val commandPort: CommandPort
) : ExecContainerService {
    override fun execCmd(application: Application, cmd: String): List<String> =
        commandPort
            .executeShellCommandWithResult(
                "docker exec ${application.containerName} sh -c 'cd / && $cmd'"
            )

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

        // Docker attach API 호출
        val execInstance = dockerClient.execCreateCmd(application.containerName)
            .withAttachStdout(true)
            .withAttachStderr(true)
            .withCmd(*cmdArray)
            .withWorkingDir(workingDir)
            .exec()


        dockerClient.execStartCmd(execInstance.id)
            .withDetach(false)
            .exec(AttachResultCallback(session))
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

    private class AttachResultCallback(
        private val session: WebSocketSession,
    ) : ResultCallback.Adapter<Frame>() {
        override fun onStart(stream: Closeable?) {
            if (session.isOpen)
                session.sendMessage(TextMessage("cmd start"))
            super.onStart(stream)
        }

        override fun onNext(frame: Frame) {
            if (session.isOpen)
                session.sendMessage(TextMessage(frame.payload))
        }

        override fun onError(throwable: Throwable) {
            throwable.printStackTrace()
            session.close()
        }

        override fun onComplete() {
            @Suppress("UNCHECKED_CAST")
            val dirStack = (session.attributes["workingDir"] as? Stack<String>) ?: Stack<String>()
            val workingDir = "/${dirStack.joinToString("/")}"

            if (session.isOpen) {
                session.sendMessage(TextMessage("current dir = $workingDir"))
                session.sendMessage(TextMessage("cmd end"))
            }
            super.onComplete()
        }
    }
}