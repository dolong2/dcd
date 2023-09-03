package com.dcd.server.persistence.auth

import com.dcd.server.core.domain.auth.model.EmailAuth
import com.dcd.server.core.domain.auth.spi.EmailAuthPort
import com.dcd.server.persistence.auth.adapter.toDomain
import com.dcd.server.persistence.auth.adapter.toEntity
import com.dcd.server.persistence.auth.repository.EmailAuthRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class EmailAuthPersistenceAdapter(
    private val emailAuthRepository: EmailAuthRepository
) : EmailAuthPort{
    override fun save(emailAuth: EmailAuth) {
        emailAuthRepository.save(emailAuth.toEntity())
    }

    override fun deleteByCode(code: String) =
        emailAuthRepository.deleteById(code)

    override fun deleteByEmailAndCode(email: String, code: String) =
        emailAuthRepository.deleteByEmailAndCode(email, code)

    override fun findByEmail(email: String): List<EmailAuth> =
        emailAuthRepository.findByEmail(email)
            .map { it.toDomain() }

    override fun findByCode(code: String): EmailAuth? =
        emailAuthRepository.findByIdOrNull(code)
            ?.toDomain()

    override fun existsByCodeAndEmail(email: String, code: String): Boolean =
        emailAuthRepository.existsByEmailAndCode(email, code)

    override fun existsByCode(code: String): Boolean =
        emailAuthRepository.existsById(code)

}