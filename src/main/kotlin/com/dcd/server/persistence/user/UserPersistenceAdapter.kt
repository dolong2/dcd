package com.dcd.server.persistence.user

import com.dcd.server.core.domain.user.model.Status
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.user.spi.UserPort
import com.dcd.server.persistence.user.adapter.toDomain
import com.dcd.server.persistence.user.adapter.toEntity
import com.dcd.server.persistence.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class UserPersistenceAdapter(
    private val userRepository: UserRepository
) : UserPort {
    override fun save(user: User) {
        userRepository.save(user.toEntity())
    }

    override fun findById(id: String): User? =
        userRepository.findByIdOrNull(id)
            ?.toDomain()

    override fun findByEmail(email: String): User? =
        userRepository.findByEmail(email)
            ?.toDomain()

    override fun existsByEmail(email: String): Boolean =
        userRepository.existsByEmail(email)

    override fun exitsById(userId: String): Boolean =
        userRepository.existsById(userId)

    override fun findByStatus(status: Status): List<User> =
        userRepository.findAllByStatus(status)
            .map { it.toDomain() }
}