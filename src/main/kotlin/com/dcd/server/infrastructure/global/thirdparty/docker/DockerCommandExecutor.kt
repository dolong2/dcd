package com.dcd.server.infrastructure.global.thirdparty.docker

import com.dcd.server.core.common.spi.ContainerPort
import com.dcd.server.core.common.spi.ContainerActions
import com.dcd.server.core.domain.application.event.ChangeApplicationStatusEvent
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.scheduler.enums.ContainerStatus
import com.dcd.server.core.domain.application.util.FailureCase
import com.dcd.server.core.domain.volume.model.VolumeMount
import com.dcd.server.infrastructure.global.thirdparty.docker.exception.DockerCommandException
import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.model.AccessMode
import com.github.dockerjava.api.model.Bind
import com.github.dockerjava.api.model.ContainerNetwork
import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.Frame
import com.github.dockerjava.api.model.HostConfig
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports
import com.github.dockerjava.api.model.Volume
import com.github.dockerjava.api.model.BuildResponseItem
import com.github.dockerjava.api.command.BuildImageResultCallback
import com.github.dockerjava.core.command.LogContainerResultCallback
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.slf4j.LoggerFactory

@Component
class DockerCommandExecutor(
    private val dockerClient: DockerClient,
    private val eventPublisher: ApplicationEventPublisher
) : ContainerPort {
    
    override fun <T> execute(action: ContainerActions.() -> T): T? {
        try {
            return DockerActionsImpl(dockerClient).action()
        } catch (ex: DockerCommandException) {
            eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.FAILURE, ex.application, ex.failureCase, ex.message))
            return null
        } catch (ex: Exception) {
            throw ex
        }
    }

    private class DockerActionsImpl(
        private val dockerClient: DockerClient
    ) : ContainerActions {
        private val PRIMARY_NETWORK = "dcd"
        
        override fun createContainer(application: Application, volumeMounts: List<VolumeMount>) {
            try {
                val exposedPort = ExposedPort.tcp(application.port)
                val portBindings = Ports()
                portBindings.bind(exposedPort, Ports.Binding.bindPort(application.externalPort))

                val binds = volumeMounts.map { mount ->
                    val accessMode =
                        if (mount.readOnly) AccessMode.ro
                        else AccessMode.rw
                    Bind(
                        mount.volume.volumeName,
                        Volume(mount.mountPath),
                        accessMode
                     )
                }
                val containerVolumes = volumeMounts.map { Volume(it.mountPath) }
                
                val response = dockerClient.createContainerCmd("${application.containerName}:${application.version}")
                    .withName(application.containerName)
                    .withNetworkMode(PRIMARY_NETWORK)
                    .withExposedPorts(exposedPort)
                    .withVolumes(containerVolumes)
                    .withHostConfig(
                        HostConfig.newHostConfig()
                            .withPortBindings(portBindings)
                            .withBinds(binds)
                    )
                    .exec()

                
                dockerClient.connectToNetworkCmd()
                    .withContainerId(response.id)
                    .withNetworkId(application.workspace.networkName)
                    .withContainerNetwork(
                        ContainerNetwork().withAliases(listOf("${application.name}"))
                    )
                    .exec()
            } catch (e: Exception) {
                throw DockerCommandException(application, FailureCase.CREATE_CONTAINER_FAILURE, e.message)
            }
        }

        override fun startContainer(application: Application) {
            try {
                dockerClient.startContainerCmd(application.containerName).exec()
            } catch (e: Exception) {
                throw DockerCommandException(application, FailureCase.RUN_CONTAINER_FAILURE, e.message)
            }
        }

        override fun stopContainer(application: Application) {
            try {
                dockerClient.stopContainerCmd(application.containerName).exec()
            } catch (e: Exception) {
                throw DockerCommandException(application, FailureCase.STOP_CONTAINER_FAILURE, e.message)
            }
        }

        override fun deleteContainer(application: Application) {
            try {
                dockerClient.removeContainerCmd(application.containerName).exec()
            } catch (e: Exception) {
                throw DockerCommandException(application, FailureCase.DELETE_CONTAINER_FAILURE, e.message)
            }
        }

        override fun getContainer(status: ContainerStatus): List<String> {
            return try {
                dockerClient.listContainersCmd()
                    .withStatusFilter(listOf(status.value))
                    .exec()
                    .map { container ->
                        val inspect = dockerClient.inspectContainerCmd(container.id).exec()
                        val name = inspect.name.replace("/", "")
                        "$name"
                    }
            } catch (e: Exception) {
                emptyList()
            }
        }

        override fun getContainerLogs(application: Application): List<String> {
            try {
                val logList = mutableListOf<String>()

                // 콜백 클래스 정의
                val callback = object : LogContainerResultCallback() {
                    override fun onNext(item: Frame?) {
                        item?.let {
                            // trimEnd를 사용해 불필요한 개행 문자를 제거
                            logList.add(String(it.payload).trimEnd())
                        }
                        super.onNext(item)
                    }
                }

                // 로그 조회 명령 설정
                dockerClient.logContainerCmd(application.containerName)
                    .withStdOut(true)    // Standard Out 포함
                    .withStdErr(true)    // Standard Error 포함
                    .withFollowStream(false) // 스트리밍이 아니라 현재 시점까지만 조회
                    .withTailAll()       // 전체 로그 조회 (필요시 .withTail(100) 등으로 조절)
                    .exec(callback)
                    .awaitCompletion()   // 로그를 모두 읽을 때까지 대기

                return logList
            } catch (e: Exception) {
                return emptyList()
            }
        }

        override fun buildImage(application: Application, dockerfilePath: String) {
            try {
                dockerClient.buildImageCmd()
                    .withDockerfile(java.io.File(dockerfilePath))
                    .withTags(setOf("${application.containerName}:${application.version}"))
                    .exec(object : BuildImageResultCallback() {
                        override fun onNext(item: BuildResponseItem?) {
                            println("빌드 중: ${item?.stream}") // 빌드 로그 출력
                            super.onNext(item)
                        }
                    })
                    .awaitCompletion(120, java.util.concurrent.TimeUnit.SECONDS)
            } catch (e: Exception) {
                throw DockerCommandException(application, FailureCase.IMAGE_BUILD_FAILURE, e.message)
            }
        }

        override fun deleteImage(application: Application) {
            try {
                dockerClient.removeImageCmd("${application.containerName}:${application.version}").exec()
            } catch (e: Exception) {
                throw DockerCommandException(application, FailureCase.DELETE_IMAGE_FAILURE, e.message)
            }
        }
    }
}