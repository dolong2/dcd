package com.dcd.server.core.common.service.impl

import com.dcd.server.core.common.service.EncryptService
import com.dcd.server.core.common.spi.EncryptPort
import org.springframework.stereotype.Service

@Service
class EncryptServiceImpl(
    private val encryptPort: EncryptPort
) : EncryptService {
    override fun encryptData(rawData: String): String =
        encryptPort.encrypt(rawData)

    override fun decryptData(encodedData: String): String =
        encryptPort.decrypt(encodedData)
}