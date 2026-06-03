package com.dcd.server.core.common.file

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import java.lang.StringBuilder

object FileContent {
    fun getApplicationDockerFileContent(
        applicationType: ApplicationType,
        version: String,
        port: Int,
        env: Map<String, String>,
        initialScripts: List<String>
    ): String =
        when(applicationType) {
            ApplicationType.SPRING_BOOT -> getSpringBootDockerFileContent(version, port, env, initialScripts)
            ApplicationType.NEST_JS -> getNestJsDockerFileContent(version, port, env, initialScripts)
            ApplicationType.GIN -> getGinDockerFileContent(version, port, env, initialScripts)
            ApplicationType.MYSQL -> getMYSQLDockerFileContent(version, port, env, initialScripts)
            ApplicationType.MARIA_DB -> getMARIADBDockerFileContent(version, port, env, initialScripts)
            ApplicationType.H2_DB -> getH2DBDockerFileContent(version, port, env, initialScripts)
            ApplicationType.REDIS -> getRedisDockerFileContent(version, port, env, initialScripts)
        }

    private fun getSpringBootDockerFileContent(version: String, port: Int, env: Map<String, String>, initialScripts: List<String>): String =
        """
        FROM amazoncorretto:${version} AS builder
        WORKDIR /builder
        COPY . .
        RUN chmod +x ./gradlew && ./gradlew bootJar
        RUN rm -f build/libs/*-plain.jar && mv build/libs/*.jar build/libs/app.jar

        FROM amazoncorretto:${version}-alpine
        WORKDIR /app
        COPY --from=builder /builder/build/libs/app.jar app.jar
        EXPOSE $port
        ${getEnvString(env)}
        ${getInitialScriptsString(initialScripts)}
        CMD ["java", "-jar", "app.jar"]
        """.trimIndent()

    private fun getNestJsDockerFileContent(version: String, port: Int, env: Map<String, String>, initialScripts: List<String>): String =
        """
        FROM node:${version} AS builder
        WORKDIR /builder
        COPY package*.json ./
        RUN npm ci
        COPY . .
        RUN npm run build

        FROM node:${version}-alpine
        WORKDIR /app
        ${getEnvString(env)}
        ${getInitialScriptsString(initialScripts)}
        COPY package*.json ./
        RUN npm ci --production=true
        COPY --from=builder /builder/dist ./dist
        EXPOSE $port
        CMD ["sh", "-c", "TZ=Asia/Seoul node dist/main.js"]
        """.trimIndent()

    private fun getGinDockerFileContent(version: String, port: Int, env: Map<String, String>, initialScripts: List<String>): String =
        """
        FROM golang:${version} AS builder
        WORKDIR /builder
        COPY go.mod go.sum ./
        RUN go mod download
        COPY . .
        RUN go mod tidy
        RUN CGO_ENABLED=0 GOOS=linux go build -a -installsuffix cgo -o main .

        FROM golang:${version}-alpine
        WORKDIR /app
        RUN apk --no-cache add ca-certificates tzdata
        ${getEnvString(env)}
        ${getInitialScriptsString(initialScripts)}
        COPY --from=builder /builder/main .
        EXPOSE $port
        CMD ["./main"]
        """.trimIndent()

    private fun getMYSQLDockerFileContent(version: String, port: Int, env: Map<String, String>, initialScripts: List<String>): String =
        """
        FROM mysql:${version}
        WORKDIR /app
        EXPOSE $port
        ${getEnvString(env)}
        ${getInitialScriptsString(initialScripts)}
        """.trimIndent()

    private fun getMARIADBDockerFileContent(version: String, port: Int, env: Map<String, String>, initialScripts: List<String>): String =
        """
        FROM mariadb:${version}
        WORKDIR /app
        EXPOSE $port
        ${getEnvString(env)}
        ${getInitialScriptsString(initialScripts)}
        """.trimIndent()

    private fun getRedisDockerFileContent(version: String, port: Int, env: Map<String, String>, initialScripts: List<String>): String =
        """
        FROM redis:${version}
        WORKDIR /app
        EXPOSE $port
        ${getEnvString(env)}
        ${getInitialScriptsString(initialScripts)}
       """.trimIndent()

    private fun getH2DBDockerFileContent(version: String, port: Int, env: Map<String, String>, initialScripts: List<String>): String =
        """
        FROM oscarfonts/h2:${version}
        WORKDIR /app
        EXPOSE $port
        ${getEnvString(env)}
        ${getInitialScriptsString(initialScripts)}
        """.trimIndent()

    fun getApplicationHttpConfig(application: Application, domain: String): String =
        """
        server {
          listen 443 ssl;
          server_name $domain;
          
          ssl_certificate /etc/nginx/conf.d/ssl/certificate/fullchain.pem;
          ssl_certificate_key /etc/nginx/conf.d/ssl/certificate/privkey.pem;
          
          location / {
            # WebSocket 관련 헤더 설정
            proxy_set_header Upgrade ${'$'}http_upgrade;
            proxy_set_header Connection ${'$'}connection_upgrade;
            proxy_set_header Host ${'$'}host;
            proxy_set_header X-Real-IP ${'$'}remote_addr;
            proxy_set_header X-Forwarded-For ${'$'}proxy_add_x_forwarded_for;
            
            proxy_pass http://${application.containerName}:${application.externalPort};
          }
        }
        """.trimIndent()

    private fun getEnvString(env: Map<String, String>): String {
        val envString = StringBuilder()
        for (it in env) {
            envString.append("ENV ${it.key}=${it.value}\n")
        }
        return envString.toString()
    }

    private fun getInitialScriptsString(initialScripts: List<String>): String {
        val initialScriptString = StringBuilder()
        for (initialScript in initialScripts) {
            initialScriptString.append("RUN $initialScript\n")
        }
        return initialScriptString.toString()
    }
}