package com.dcd.server.core.common.file

import com.dcd.server.core.domain.application.model.Application
import java.lang.StringBuilder

object FileContent {
    fun getSpringBootDockerFileContent(version: String, port: Int, env: Map<String, String>): String =
        """
        FROM openjdk:${version}-jdk
        COPY build/libs/*.jar build/libs/
        RUN rm -f build/libs/*-plain.jar
        RUN mv build/libs/*.jar build/libs/app.jar
        EXPOSE $port
        ${getEnvString(env)}
        CMD ["java", "-jar", "build/libs/app.jar"]
        """.trimIndent()

    fun getNestJsDockerFileContent(version: String, port: Int, env: Map<String, String>): String =
        """
        FROM node:${version}
        COPY . .
        RUN npm install
        RUN npm run build
        EXPOSE $port
        ${getEnvString(env)}
        CMD ["npm", "start"]
        """.trimIndent()

    fun getMYSQLDockerFileContent(version: String, port: Int, env: Map<String, String>): String =
        """
        FROM mysql:${version}
        EXPOSE $port
        ${getEnvString(env)}
        """.trimIndent()

    fun getMARIADBDockerFileContent(version: String, port: Int, env: Map<String, String>): String =
        """
        FROM mariadb:${version}
        EXPOSE $port
        ${getEnvString(env)}
        """.trimIndent()

    fun getRedisDockerFileContent(version: String, port: Int, env: Map<String, String>): String =
        """
        FROM redis:${version}
        EXPOSE $port
        ${getEnvString(env)}
       """.trimIndent()

    fun getH2DBDockerFileContent(version: String, port: Int, env: Map<String, String>): String =
        """
        FROM oscarfonts/h2:${version}
        EXPOSE $port
        ${getEnvString(env)}
        """.trimIndent()

    fun getImageVersionShellScriptContent(imageName: String, minVersion: String): String =
        """
        #!/bin/bash
    
        # 이미지, 페이지 사이즈, 최소 버전(threshold) 설정
        IMAGE_NAME="library/$imageName"
        PAGE_SIZE=100
        MIN_VERSION="$minVersion"
    
        # 첫 페이지 URL 구성
        URL="https://hub.docker.com/v2/repositories/${'$'}IMAGE_NAME/tags/?page_size=${'$'}PAGE_SIZE"
    
        # 결과를 저장할 변수 및 배열 초기화
        LATEST_FOUND=false
        NUMERIC_TAGS=()
    
        # pagination 처리: next URL이 없을 때까지 반복
        while [ -n "${'$'}URL" ] && [ "${'$'}URL" != "null" ]; do
            RESPONSE=\$(curl -s "${'$'}URL")
            
            # 현재 페이지의 태그 목록 추출
            TAGS=\$(echo "${'$'}RESPONSE" | jq -r '.results[].name')
            
            for tag in ${'$'}TAGS; do
                # latest는 따로 체크
                if [[ "${'$'}tag" == "latest" ]]; then
                    LATEST_FOUND=true
                # 숫자와 점(.)만 포함된 태그 필터링
                elif [[ "${'$'}tag" =~ ^[0-9]+(\.[0-9]+)*\$ ]]; then
                    # 버전 비교: tag가 MIN_VERSION 이상이면 저장
                    # sort -V를 사용하여 두 버전을 정렬한 후 첫 번째가 MIN_VERSION이면 tag가 MIN_VERSION 이상입니다.
                    lowest=\$(printf "%s\n%s" "${'$'}MIN_VERSION" "${'$'}tag" | sort -V | head -n1)
                    if [ "${'$'}lowest" = "${'$'}MIN_VERSION" ]; then
                        NUMERIC_TAGS+=("${'$'}tag")
                    fi
                fi
            done
            
            # 다음 페이지 URL 추출 (없으면 "null" 또는 빈 문자열)
            URL=\$(echo "${'$'}RESPONSE" | jq -r '.next')
        done
    
        # 최신 태그(latest)가 있으면 제일 먼저 출력
        if ${'$'}LATEST_FOUND; then
            echo "latest"
        fi
    
        # 숫자 태그를 내림차순(-r 옵션) 버전 정렬(-V 옵션) 후 출력
        if [ ${'$'}{#NUMERIC_TAGS[@]} -gt 0 ]; then
            sorted_numeric_tags=\$(printf "%s\n" "${'$'}{NUMERIC_TAGS[@]}" | sort -r -V)
            echo "${'$'}sorted_numeric_tags"
        fi
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

}