package com.dcd.server.core.domain.application.service

interface ExistsPortService {
    fun existsPort(port: Int): Boolean
}