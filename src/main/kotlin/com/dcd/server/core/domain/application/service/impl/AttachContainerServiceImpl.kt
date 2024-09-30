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

        // Docker attach API 호출
        val execInstance = dockerClient.execCreateCmd(containerName)
            .withAttachStdout(true)
            .withAttachStderr(true)
            .withCmd(*cmdArray)
            .withWorkingDir("/")
            .apply {

                session.attributes["workingDir"] = this.workingDir
            }
            .exec()


        dockerClient.execStartCmd(execInstance.id)
            .withDetach(false)
            .exec(AttachResultCallback(session))

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