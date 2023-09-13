package com.dcd.server.core.common.cmd

object CreateFileCommand {
    fun getSpringBootDockerFileContent(name: String): String =
        "FROM openjdk:17-jdk\n" +
        "WORKDIR /usr/src/app\n" +
        "COPY $name/build/libs/$name-0.0.1-SNAPSHOT.jar $name/build/libs/app-0.0.1-SNAPSHOT.jar\n" +
        "EXPOSE 8080\n" +
        "CMD [\"java\",\"-jar\",\"$name/build/libs/app-0.0.1-SNAPSHOT.jar\"]"

}