package com.dcd.server.core.domain.domain.model

import com.dcd.server.core.domain.application.model.Application

data class Domain(
    val id: String,
    val name: String,
    val description: String,
    val application: Application?
)
