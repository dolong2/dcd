package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.domain.application.service.ExistsPortService
import com.dcd.server.core.domain.application.service.GetExternalPortService
import org.springframework.stereotype.Service

@Service
class GetExternalServiceImpl(
    private val existsPortService: ExistsPortService,
) : GetExternalPortService {
    override fun getExternalPort(port: Int): Int {
        var externalPort = port
        while (existsPortService.existsPort(externalPort)) {
            externalPort += 1
        }
        return externalPort
    }
}