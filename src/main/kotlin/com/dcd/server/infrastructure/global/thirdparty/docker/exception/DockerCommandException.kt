package com.dcd.server.infrastructure.global.thirdparty.docker.exception

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.util.FailureCase

class DockerCommandException(application: Application, failureCase: FailureCase, message: String?) : RuntimeException(message)