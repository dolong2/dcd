package com.dcd.server.core.domain.auth.service

import com.dcd.server.core.domain.auth.model.enums.EmailAuthUsage
import com.dcd.server.core.domain.auth.service.impl.EmailSendServiceImpl
import com.dcd.server.core.domain.auth.spi.CommandEmailAuthPort
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender

class EmailSendServiceTest : BehaviorSpec({
    val commandEmailAuthPort = mockk<CommandEmailAuthPort>(relaxed = true)
    val mailSender = mockk<JavaMailSender>(relaxed = true)
    val emailSendService = EmailSendServiceImpl(commandEmailAuthPort, mailSender)

    given("이메일이 주어지고") {
        val testEmail = "testEmail"
        `when`("이메일을 보낼때") {
            emailSendService.sendEmail(testEmail, EmailAuthUsage.SIGNUP)
            every { commandEmailAuthPort.save(any()) } returns Unit
            then("메일을 보내고 mailAuth를 save해야함") {
                verify { mailSender.send(any<MimeMessage>()) }
                verify { commandEmailAuthPort.save(any()) }
            }
        }
    }
})