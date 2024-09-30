package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.service.ExecContainerService
import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.model.Frame
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.io.*

@Service
class ExecContainerServiceImpl(
    private val dockerClient: DockerClient,
) : ExecContainerService {
    override fun execCmd(application: Application, session: WebSocketSession, cmd: String) {
        val containerName = application.name.lowercase()

        val cmdArray = cmd.split(" ").toTypedArray()

        if (cmd.contains("cd")) {
            val newDir = cmdArray[1]
            val currentDir = session.attributes["workingDir"] as? String ?: "/"
            val updatedDir = updateWorkingDir(currentDir, newDir)
            session.attributes["workingDir"] = updatedDir
        }

        val workingDir = session.attributes["workingDir"] as? String ?: "/"

        // Docker attach API 호출
        val execInstance = dockerClient.execCreateCmd(containerName)
            .withAttachStdout(true)
            .withAttachStderr(true)
            .withCmd(*cmdArray)
            .withWorkingDir(workingDir)
            .exec()


        dockerClient.execStartCmd(execInstance.id)
            .withDetach(false)
            .exec(AttachResultCallback(session))

    }

    private fun updateWorkingDir(currentDir: String, newDir: String): String {
        return when {
            newDir == "/" -> "/"  // 루트 디렉토리로 이동
            newDir == ".." -> currentDir.substringBeforeLast("/", "/")  // 상위 디렉토리로 이동
            newDir.startsWith("/") -> newDir  // 절대 경로로 설정
            else -> {
                if (currentDir != "/")
                    "$currentDir/$newDir"
                else "/${newDir}"
            }  // 현재 디렉토리에서 하위 디렉토리로 이동
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
            if (session.isOpen) {
                session.sendMessage(TextMessage("current dir = ${session.attributes["workingDir"] ?: "/"}"))
                session.sendMessage(TextMessage("cmd end"))
            }
            super.onComplete()
        }
    }
}