package com.dcd.server.core.domain.auth.service.impl

import com.dcd.server.core.domain.auth.model.EmailAuth
import com.dcd.server.core.domain.auth.model.enums.EmailAuthUsage
import com.dcd.server.core.domain.auth.service.EmailSendService
import com.dcd.server.core.domain.auth.spi.CommandEmailAuthPort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class EmailSendServiceImpl(
    private val commandEmailAuthPort: CommandEmailAuthPort,
    private val emailSender: JavaMailSender,
) : EmailSendService{
    override suspend fun sendEmail(email: String, usage: EmailAuthUsage) {
        val emailAuth = EmailAuth(email = email, usage = usage)
        val code = emailAuth.code

        withContext(Dispatchers.IO) {
            val message = emailSender.createMimeMessage()
            val messageHelper = MimeMessageHelper(message, "UTF-8")
            messageHelper.setTo(email)
            messageHelper.setSubject("[DCD] 메일인증 코드")
            messageHelper.setText("$code \n위의 코드를 입력해주세요!")
            emailSender.send(message)
        }

        commandEmailAuthPort.save(emailAuth)
    }
}