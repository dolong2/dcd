package com.dcd.server.core.domain.application.util

enum class FailureCase(
    val reason: String
) {
    CLONE_FAILURE("애플리케이션 복사중 에러"),
    CREATE_DOCKER_FILE_FAILURE("도커파일 생성중 에러"),
    IMAGE_BUILD_FAILURE("이미지 빌드중 에러"),
    CREATE_CONTAINER_FAILURE("컨테이너 생성시 에러"),
    RUN_CONTAINER_FAILURE("컨테이너 실행중 에러"),
    STOP_CONTAINER_FAILURE("컨테이너 정지중 에러"),
    CONNECT_NETWORK_FAILURE("네트워크 연결중 에러"),
    CREATE_DIRECTORY_FAILURE("애플리케이션 디렉토리 생성중 에러"),
    DELETE_DIRECTORY_FAILURE("애플리케이션 디렉토리 삭제중 에러"),
    DELETE_CONTAINER_FAILURE("컨테이너 삭제중 에러"),
    DELETE_IMAGE_FAILURE("이미지 삭제중 에러"),
}