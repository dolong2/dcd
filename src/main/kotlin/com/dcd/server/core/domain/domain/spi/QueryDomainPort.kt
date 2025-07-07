package com.dcd.server.core.domain.domain.spi

import com.dcd.server.core.domain.domain.model.Domain

interface QueryDomainPort {
    fun findAll(): List<Domain>
    fun findById(id: String): Domain?
}