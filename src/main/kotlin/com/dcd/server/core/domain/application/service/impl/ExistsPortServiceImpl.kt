package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.service.ExistsPortService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import org.springframework.stereotype.Service

@Service
class ExistsPortServiceImpl (
    private val queryApplicationPort: QueryApplicationPort,
    private val commandPort: CommandPort
) : ExistsPortService {
    override fun existsPort(port: Int): Boolean {
        val commandResult = commandPort.executeShellCommand("lsof -i :${port}").result
        return commandResult.isNotEmpty() || queryApplicationPort.existsByExternalPort(port)
    }
}