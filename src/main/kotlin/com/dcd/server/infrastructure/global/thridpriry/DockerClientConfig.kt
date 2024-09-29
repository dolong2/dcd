package com.dcd.server.infrastructure.global.thridpriry

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientBuilder
import com.github.dockerjava.core.DockerClientConfig
import com.github.dockerjava.okhttp.OkDockerHttpClient
import com.github.dockerjava.transport.DockerHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class DockerClientConfig {
    @Bean
    fun dockerClient(): DockerClient {
        val config = getDockerConfig()

        val httpClient: DockerHttpClient = OkDockerHttpClient.Builder()
            .dockerHost(config.dockerHost)
            .sslConfig(config.sslConfig)
            .build()

        return DockerClientBuilder
            .getInstance(config)
            .withDockerHttpClient(httpClient)
            .build()
    }

    private fun getDockerConfig(): DockerClientConfig =
        DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withDockerHost("unix:///var/run/docker.sock")
            .build()
}