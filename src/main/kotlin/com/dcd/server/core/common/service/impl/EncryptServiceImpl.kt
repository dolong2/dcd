package com.dcd.server.core.common.service.impl

import com.dcd.server.core.common.service.EncryptService
import org.springframework.stereotype.Service

@Service
class EncryptServiceImpl(

) : EncryptService {
    override fun encryptData(rawData: String): String {
        TODO("Not yet implemented")
    }

    override fun decryptData(encodedData: String): String {
        TODO("Not yet implemented")
    }
}