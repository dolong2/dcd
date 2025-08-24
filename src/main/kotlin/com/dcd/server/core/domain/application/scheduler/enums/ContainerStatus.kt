package com.dcd.server.core.domain.application.scheduler.enums

enum class ContainerStatus(
    val value: String
) {
    EXITED("exited"),
    RUNNING("running"),
    CREATED("created")
}