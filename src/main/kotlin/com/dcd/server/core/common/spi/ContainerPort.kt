package com.dcd.server.core.common.spi

interface ContainerPort {
    fun <T> execute(action: ContainerActions.() -> T): T
}