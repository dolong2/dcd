package com.dcd.server.presentation.common.annotation

import org.springframework.core.annotation.AliasFor
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
annotation class WebAdapter(
    @get:AliasFor(annotation = RequestMapping::class)
    val value: String = ""
)
