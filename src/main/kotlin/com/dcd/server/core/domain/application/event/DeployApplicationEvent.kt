package com.dcd.server.core.domain.application.event

import com.dcd.server.core.domain.application.model.Application

class DeployApplicationEvent(
    val applications: List<Application>,
)