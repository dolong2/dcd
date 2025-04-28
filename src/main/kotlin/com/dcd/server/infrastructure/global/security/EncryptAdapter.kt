package com.dcd.server.infrastructure.global.security

import com.dcd.server.core.common.spi.EncryptPort
import com.dcd.server.infrastructure.global.security.properties.AesProperty
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.Cipher

@Component
class EncryptAdapter(
    private val aesProperty: AesProperty
) : EncryptPort {
    override fun encrypt(rawData: String): String {
        val cipher = Cipher.getInstance(aesProperty.algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, aesProperty.keySpec, aesProperty.ivSpec)
        val encrypted = cipher.doFinal(rawData.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(encrypted)
    }

    override fun decrypt(encodedData: String): String {
        val cipher = Cipher.getInstance(aesProperty.algorithm)
        cipher.init(Cipher.DECRYPT_MODE, aesProperty.keySpec, aesProperty.ivSpec)
        val decodedBytes = Base64.getDecoder().decode(encodedData)
        val decrypted = cipher.doFinal(decodedBytes)
        return String(decrypted, Charsets.UTF_8)
    }
}