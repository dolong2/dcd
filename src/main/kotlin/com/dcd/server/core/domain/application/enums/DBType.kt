package com.dcd.server.core.domain.application.enums

enum class DBType(description: String) {
    MYSQL("mysql-sql"),
    MARIADB("mariadb-sql"),
    REDIS("redis-nosql")
}