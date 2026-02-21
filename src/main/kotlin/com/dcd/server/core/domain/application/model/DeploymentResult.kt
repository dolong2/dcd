package com.dcd.server.core.domain.application.model

import com.dcd.server.core.domain.application.util.FailureCase

sealed class DeploymentResult(
    val failureCase: FailureCase?,
    val failureReasonDetail: String?,
) {
    object SUCCESS : DeploymentResult(failureCase = null, failureReasonDetail = null)
    class ERROR(failureCase: FailureCase, detail: String?) : DeploymentResult(failureCase = failureCase, failureReasonDetail = detail)
}