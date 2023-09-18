package com.dcd.server.core.common.cmd

object FileContent {
    fun getSpringBootDockerFileContent(name: String, javaVersion: Int): String =
        "FROM openjdk:${javaVersion}-jdk\n" +
        "WORKDIR /usr/src/app\n" +
        "COPY build/libs/$name.jar build/libs/app.jar\n" +
        "EXPOSE 8080\n" +
        "CMD [\"java\",\"-jar\",\"$name/build/libs/app.jar\"]"

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

    fun getServerComposeContent(): String =
        "version: \"3.7\"\n" +
        "networks:\n" +
        "\tbackend:\n" +
        "\t\tdriver: bridge\n" +
        "services:\n"

    fun getMySqlDockerComposeContent(rootPassword: String, dataBaseName: String): String =
        "\tmysql:\n" +
        "\t\timage: mysql:latest\n" +
        "\t\tports:\n" +
        "\t\t\t- 3306:3306\n" +
        "\t\tnetworks:\n" +
        "\t\t\t- backend\n" +
        "\t\tenvironment:\n" +
        "\t\t\tMYSQL_ROOT_PASSWORD: ${rootPassword}\n" +
        "\t\t\tMYSQL_DATABASE: ${dataBaseName}\n"

    fun getMariaDBComposeContent(rootPassword: String, dataBaseName: String): String =
        "\tmariadb:\n" +
        "\t\timage: mariadb:latest\n" +
        "\t\tports:\n" +
        "\t\t\t- 3306:3306\n" +
        "\t\tnetworks:\n" +
        "\t\t\t- backend\n" +
        "\t\tenvironment:\n" +
        "\t\t\tMYSQL_ROOT_PASSWORD: ${rootPassword}\n" +
        "\t\t\tMYSQL_DATABASE: ${dataBaseName}\n"

    fun getRedisComposeContent(): String =
        "\tredis:\n" +
        "\t\timage: redis:latest\n"+
        "\t\tports:\n" +
        "\t\t\t- 6379:6379\n" +
        "\t\tnetworks:\n" +
        "\t\t\t- backend\n"

}