package com.dcd.server.core.domain.domain.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.domain.exception.AlreadyConnectedDomainException
import com.dcd.server.core.domain.domain.exception.DomainNotFoundException
import com.dcd.server.core.domain.domain.spi.CommandDomainPort
import com.dcd.server.core.domain.domain.spi.QueryDomainPort

@UseCase
class DeleteDomainUseCase(
    private val queryDomainPort: QueryDomainPort,
    private val commandDomainPort: CommandDomainPort
) {
    fun execute(id: String) {
        val domain = (queryDomainPort.findById(id)
            ?: throw DomainNotFoundException())

        if (domain.application != null)
            throw AlreadyConnectedDomainException()

        commandDomainPort.delete(domain)
    }
}