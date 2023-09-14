package com.dcd.server.core.common.cmd

object FileContent {
    fun getSpringBootDockerFileContent(name: String, javaVersion: Int): String =
        "FROM openjdk:${javaVersion}-jdk\n" +
        "WORKDIR /usr/src/app\n" +
        "COPY $name/build/libs/$name-0.0.1-SNAPSHOT.jar $name/build/libs/app-0.0.1-SNAPSHOT.jar\n" +
        "EXPOSE 8080\n" +
        "CMD [\"java\",\"-jar\",\"$name/build/libs/app-0.0.1-SNAPSHOT.jar\"]"

}