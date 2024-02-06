package com.dcd.server.core.domain.application.service

interface GetExternalPortService {
    fun getExternalPort(port: Int): Int

}