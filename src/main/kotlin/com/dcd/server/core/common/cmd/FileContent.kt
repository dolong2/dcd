package com.dcd.server.core.common.cmd

object FileContent {
    fun getSpringBootDockerFileContent(name: String, javaVersion: Int): String =
        "FROM openjdk:${javaVersion}-jdk\n" +
        "WORKDIR /usr/src/app\n" +
        "COPY build/libs/$name-0.0.1-SNAPSHOT.jar build/libs/app-0.0.1-SNAPSHOT.jar\n" +
        "EXPOSE 8080\n" +
        "CMD [\"java\",\"-jar\",\"$name/build/libs/app-0.0.1-SNAPSHOT.jar\"]"

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

}