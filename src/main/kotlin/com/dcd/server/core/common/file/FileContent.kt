package com.dcd.server.core.common.file

import com.dcd.server.core.domain.application.model.Application
import java.lang.StringBuilder

object FileContent {
    fun getSpringBootDockerFileContent(version: String, port: Int, env: Map<String, String>): String =
        "FROM openjdk:${version}-jdk\n" +
        "COPY build/libs/*.jar build/libs/\n" +
        "RUN rm -f build/libs/*-plain.jar\n" +
        "RUN mv build/libs/*.jar build/libs/app.jar\n" +
        "EXPOSE ${port}\n" +
        getEnvString(env) +
        "CMD [\"java\",\"-jar\",\"build/libs/app.jar\"]"

    fun getNestJsDockerFileContent(version: String, port: Int, env: Map<String, String>): String =
        "FROM node:${version}\n" +
        "COPY . .\n" +
        "RUN npm install\n" +
        "RUN npm run build\n" +
        "EXPOSE ${port}\n" +
        getEnvString(env) +
        "CMD [\"npm\", \"start\"]"

    fun getMYSQLDockerFileContent(version: String, port: Int, env: Map<String, String>): String =
        "FROM mysql:${version}\n" +
        "EXPOSE ${port}\n" +
        getEnvString(env)

    fun getMARIADBDockerFileContent(version: String, port: Int, env: Map<String, String>): String =
        "FROM mariadb:${version}\n" +
        "EXPOSE ${port}\n" +
        getEnvString(env)

    fun getRedisDockerFileContent(version: String, port: Int, env: Map<String, String>): String =
        "FROM redis:${version}\n" +
        "EXPOSE ${port}\n" +
        getEnvString(env)

    fun getBuildGradleKtsFileContent(name: String): String =
        "tasks {\n" +
        "\tval customBootJarName = \"$name.jar\"\n" +
        "\tnamed<org.springframework.boot.gradle.tasks.bundling.BootJar>(\"bootJar\") {\n" +
        "\t\tarchiveFileName.set(customBootJarName)\n" +
        "\t}\n" +
        "}"

    fun getBuildGradleFileContent(name: String): String =
        "bootJar {\n" +
        "\tarchiveFileName = '$name.jar'\n" +
        "}"

    fun getSSLYmlFileContent(name: String, password: String): String =
        "server:\n" +
        "\tssl:\n" +
        "\t\tkey-store: classpath:${name}.p12\n" +
        "\t\tkey-store-type: PKC12\n" +
        "\t\tkey-store-password: $password"

    fun getSSLPropertyFileContent(name: String, password: String): String =
        "server.ssl.key-store=classpath:${name}.p12\n" +
        "server.ssl.key-store-type: PKC12\n" +
        "server.ssl.key-store-password: $password"

    fun getApplicationHttpConfig(application: Application, domain: String): String =
        "server {" +
            "\tlisten: 80;\n" +
            "\tserver-name: $domain;\n\n" +
            "\tlocation / {\n" +
                "\t\tproxy_pass: http://localhost:${application.externalPort};\n" +
            "\t}\n" +
        "}"

    private fun getEnvString(env: Map<String, String>): String {
        val envString = StringBuilder()
        for (it in env) {
            envString.append("ENV ${it.key}=${it.value}\n")
        }
        return envString.toString()
    }

}