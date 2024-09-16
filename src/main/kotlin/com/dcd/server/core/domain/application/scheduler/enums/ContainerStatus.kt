package com.dcd.server.core.domain.application.scheduler.enums

enum class ContainerStatus(
    val description: String
) {
    EXITED("exited"),
    RUNNING("running"),
    CREATED("created")
}