package com.dcd.server.core.common.service

interface EncryptService {
    fun encryptData(rawData: String): String
    fun decryptData(encodedData: String): String
}