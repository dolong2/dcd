package com.dcd.server.persistence.auth.repository

import com.dcd.server.persistence.auth.entity.TokenBlackListEntity
import org.springframework.data.repository.CrudRepository

interface TokenBlackListRepository : CrudRepository<TokenBlackListEntity, String> {
}