package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.service.AttachContainerService
import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.model.Frame
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.io.*

@Service
class AttachContainerServiceImpl(
    private val dockerClient: DockerClient,
) : AttachContainerService {
    override fun attachService(application: Application, session: WebSocketSession, cmd: String) {
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

    class AttachResultCallback(
        private val session: WebSocketSession,
    ) : ResultCallback.Adapter<Frame>() {
        override fun onNext(frame: Frame) {
            println(frame.streamType)
            try {
                if (session.isOpen)
                    // 프레임 데이터를 WebSocket을 통해 클라이언트에 전달
                    session.sendMessage(TextMessage(frame.payload))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        override fun onError(throwable: Throwable) {
            throwable.printStackTrace()
            session.close()
        }
    }
}