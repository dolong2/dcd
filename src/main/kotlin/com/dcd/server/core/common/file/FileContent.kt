package com.dcd.server.core.common.file

object FileContent {
    fun getSpringBootDockerFileContent(name: String, javaVersion: Int): String =
        "FROM openjdk:${javaVersion}-jdk\n" +
        "COPY build/libs/$name.jar build/libs/app.jar\n" +
        "EXPOSE 8080\n" +
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

    fun getServerComposeContent(): String =
        "version: \"3.7\"\n" +
        "networks:\n" +
        " backend:\n" +
        "  driver: bridge\n" +
        "services:\n"

    fun getMySqlDockerComposeContent(rootPassword: String, dataBaseName: String): String =
        " mysql:\n" +
        "  image: mysql:latest\n" +
        "  ports:\n" +
        "   - 3306:3306\n" +
        "  networks:\n" +
        "   - backend\n" +
        "  environment:\n" +
        "   MYSQL_ROOT_PASSWORD: ${rootPassword}\n" +
        "   MYSQL_DATABASE: ${dataBaseName}\n"

    fun getMariaDBComposeContent(rootPassword: String, dataBaseName: String): String =
        " mariadb:\n" +
        "  image: mariadb:latest\n" +
        "  ports:\n" +
        "   - 3306:3306\n" +
        "  networks:\n" +
        "   - backend\n" +
        "  environment:\n" +
        "   MYSQL_ROOT_PASSWORD: ${rootPassword}\n" +
        "   MYSQL_DATABASE: ${dataBaseName}\n"

    fun getRedisComposeContent(): String =
        " redis:\n" +
        "  image: redis:latest\n"+
        "  ports:\n" +
        "   - 6379:6379\n" +
        "  networks:\n" +
        "   - backend\n"

    fun getApplicationComposeContent(applicationName: String, port: Int): String =
        " ${applicationName.lowercase()}:\n" +
                "  image: ${applicationName.lowercase()}:latest\n" +
                "  ports:\n" +
                "   - $port:$port\n" +
                "  networks:\n" +
                "   - backend\n"

}