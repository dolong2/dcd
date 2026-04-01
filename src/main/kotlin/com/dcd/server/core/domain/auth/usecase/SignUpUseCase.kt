package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.core.common.annotation.CheckEmailCertificate
import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.service.SecurityService
import com.dcd.server.core.common.spi.CountBloomFilterPort
import com.dcd.server.core.domain.auth.dto.extension.toEntity
import com.dcd.server.core.domain.auth.dto.request.SignUpReqDto
import com.dcd.server.core.domain.auth.exception.AlreadyExistsUserException
import com.dcd.server.core.domain.auth.model.enums.EmailAuthUsage
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.user.spi.CommandUserPort
import com.dcd.server.core.domain.user.spi.QueryUserPort

@UseCase
class SignUpUseCase(
    private val securityService: SecurityService,
    private val commandUserPort: CommandUserPort,
    private val queryUserPort: QueryUserPort,
    private val bloomFilterPort: CountBloomFilterPort
) {
    @CheckEmailCertificate("#signUpReqDto.email", EmailAuthUsage.SIGNUP)
    fun execute(signUpReqDto: SignUpReqDto) {
        val email = signUpReqDto.email

        //유저 존재 여부 검사시 bloomFilter의 존재여부 판단을 우선적으로 판단함
        val bloomFilterQueryResult = bloomFilterPort.exists(User.USER_INFO_BLOOM_FILTER, email)
        if(bloomFilterQueryResult && queryUserPort.existsByEmail(email))
            throw AlreadyExistsUserException()
        val encodePassword = securityService.encodePassword(signUpReqDto.password)
        val user = signUpReqDto.toEntity(encodePassword)
        commandUserPort.save(user)
    }
}