package com.dcd.server.core.domain.application.model

import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.user.model.User
import java.util.*

data class Application(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String?,
    val applicationType: ApplicationType,
    val githubUrl: String,
    val owner: User
)