package com.dcd.server.infrastructure.global.security.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@ConfigurationProperties("aes")
class AesProperty(
    secretKey: String,
    initVector: String
) {
    private val keyAlgorithm: String = "AES"
    val algorithm = "AES/CBC/PKCS5Padding"
    val keySpec: SecretKey = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), keyAlgorithm)
    val ivSpec: IvParameterSpec = IvParameterSpec(initVector.toByteArray(Charsets.UTF_8))
}