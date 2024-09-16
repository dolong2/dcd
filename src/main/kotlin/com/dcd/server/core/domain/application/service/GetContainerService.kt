package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.scheduler.enums.ContainerStatus

interface GetContainerService {
    fun getContainerNameByStatus(status: ContainerStatus): List<String>
}