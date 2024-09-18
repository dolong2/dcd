package com.dcd.server.core.domain.application.model.enums

enum class ApplicationStatus(description: String) {
    CREATED("애플리케이션이 생성됨"),
    PENDING("애플리케이션 상태 보류중 ex). 실행되기전, 정지되기전"),
    RUNNING("애플리케이션 실행중"),
    STOPPED("애플리케이션이 정지됨"),
    FAILURE("애플리케이션의 플로우중 실패함")
}