package com.dcd.server.core.domain.domain.spi

import com.dcd.server.core.domain.domain.model.Domain

interface CommandDomainPort {
    fun save(domain: Domain)
    fun delete(domain: Domain)
    fun saveAll(domainList: List<Domain>)
}