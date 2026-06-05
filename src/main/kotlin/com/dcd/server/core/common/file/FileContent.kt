package com.dcd.server.core.common.file

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import java.lang.StringBuilder

object FileContent {


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
}