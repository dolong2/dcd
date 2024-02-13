package com.dcd.server.core.common.file

import java.lang.StringBuilder

object FileContent {
    fun getSpringBootDockerFileContent(name: String, javaVersion: String, port: Int, env: Map<String, String>): String =
        "FROM openjdk:${javaVersion}-jdk\n" +
        "COPY build/libs/$name.jar build/libs/app.jar\n" +
        "EXPOSE ${port}\n" +
        getEnvString(env) +
        "CMD [\"java\",\"-jar\",\"build/libs/app.jar\"]"

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
        "server:" +
        "\tssl:" +
        "\t\tkey-store: classpath:${name}.p12" +
        "\t\tkey-store-type: PKC12" +
        "\t\tkey-store-password: $password"

    private fun getEnvString(env: Map<String, String>): String {
        val envString = StringBuilder()
        for (it in env) {
            envString.append("ENV ${it.key} ${it.value}\n")
        }
        return envString.toString()
    }

}