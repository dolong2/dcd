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
        CMD [\"java\",\"-jar\",\"build/libs/app.jar\"]
        """.trimIndent()

    fun getNestJsDockerFileContent(version: String, port: Int, env: Map<String, String>): String =
        """
        FROM node:${version}
        COPY . .
        RUN npm install
        RUN npm run build
        EXPOSE $port
        ${getEnvString(env)}
        CMD [\"npm\", \"start\"]
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

    fun getImageVersionShellScriptContent(imageName: String, minVersion: String): String =
        "#!/bin/bash\n" +
        "\n" +
        "# 이미지, 페이지 사이즈, 최소 버전(threshold) 설정\n" +
        "IMAGE_NAME=\"library/$imageName\"\n" +
        "PAGE_SIZE=100\n" +
        "MIN_VERSION=\"$minVersion\"\n" +
        "\n" +
        "# 첫 페이지 URL 구성\n" +
        "URL=\"https://hub.docker.com/v2/repositories/\$IMAGE_NAME/tags/?page_size=\$PAGE_SIZE\"\n" +
        "\n" +
        "# 결과를 저장할 변수 및 배열 초기화\n" +
        "LATEST_FOUND=false\n" +
        "NUMERIC_TAGS=()\n" +
        "\n" +
        "# pagination 처리: next URL이 없을 때까지 반복\n" +
        "while [ -n \"\$URL\" ] && [ \"\$URL\" != \"null\" ]; do\n" +
        "    RESPONSE=\$(curl -s \"\$URL\")\n" +
        "    \n" +
        "    # 현재 페이지의 태그 목록 추출\n" +
        "    TAGS=\$(echo \"\$RESPONSE\" | jq -r '.results[].name')\n" +
        "    \n" +
        "    for tag in \$TAGS; do\n" +
        "        # latest는 따로 체크\n" +
        "        if [[ \"\$tag\" == \"latest\" ]]; then\n" +
        "            LATEST_FOUND=true\n" +
        "        # 숫자와 점(.)만 포함된 태그 필터링\n" +
        "        elif [[ \"\$tag\" =~ ^[0-9]+(\\.[0-9]+)*\$ ]]; then\n" +
        "            # 버전 비교: tag가 MIN_VERSION 이상이면 저장\n" +
        "            # sort -V를 사용하여 두 버전을 정렬한 후 첫 번째가 MIN_VERSION이면 tag가 MIN_VERSION 이상입니다.\n" +
        "            lowest=\$(printf \"%s\\n%s\" \"\$MIN_VERSION\" \"\$tag\" | sort -V | head -n1)\n" +
        "            if [ \"\$lowest\" = \"\$MIN_VERSION\" ]; then\n" +
        "                NUMERIC_TAGS+=(\"\$tag\")\n" +
        "            fi\n" +
        "        fi\n" +
        "    done\n" +
        "    \n" +
        "    # 다음 페이지 URL 추출 (없으면 \"null\" 또는 빈 문자열)\n" +
        "    URL=\$(echo \"\$RESPONSE\" | jq -r '.next')\n" +
        "done\n" +
        "\n" +
        "# 최신 태그(latest)가 있으면 제일 먼저 출력\n" +
        "if \$LATEST_FOUND; then\n" +
        "    echo \"latest\"\n" +
        "fi\n" +
        "\n" +
        "# 숫자 태그를 내림차순(-r 옵션) 버전 정렬(-V 옵션) 후 출력\n" +
        "if [ \${#NUMERIC_TAGS[@]} -gt 0 ]; then\n" +
        "    sorted_numeric_tags=\$(printf \"%s\\n\" \"\${NUMERIC_TAGS[@]}\" | sort -r -V)\n" +
        "    echo \"\$sorted_numeric_tags\"\n" +
        "fi\n"

    fun getApplicationHttpConfig(application: Application, domain: String): String =
        "server {\n" +
            "\tlisten 443 ssl;\n" +
            "\tserver_name $domain;\n\n" +

            "\tssl_certificate /etc/nginx/conf.d/ssl/certificate/fullchain.pem;\n" +
            "\tssl_certificate_key /etc/nginx/conf.d/ssl/certificate/privkey.pem;\n\n" +

            "\tlocation / {\n" +
                "\t\t# WebSocket 관련 헤더 설정\n" +
                "\t\tproxy_set_header Upgrade \$http_upgrade;\n" +
                "\t\tproxy_set_header Connection \$connection_upgrade;\n" +
                "\t\tproxy_set_header Host \$host;\n" +
                "\t\tproxy_set_header X-Real-IP \$remote_addr;\n" +
                "\t\tproxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;\n\n" +

                "\t\tproxy_pass http://host.docker.internal:${application.externalPort};\n" +
            "\t}\n" +
        "}\n"

    private fun getEnvString(env: Map<String, String>): String {
        val envString = StringBuilder()
        for (it in env) {
            envString.append("ENV ${it.key}=${it.value}\n")
        }
        return envString.toString()
    }

}