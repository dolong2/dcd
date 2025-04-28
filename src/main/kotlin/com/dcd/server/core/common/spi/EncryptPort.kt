package com.dcd.server.core.common.spi

interface EncryptPort {
    fun encrypt(rawData: String): String
    fun decrypt(encodedData: String): String
}