package com.dcd.server.persistence.common.extension

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.util.*

class UUIDExtensionTest : BehaviorSpec({
    given("올바른 형식의 UUID 문자열이 주어지고") {
        val uuid = UUID.randomUUID()

        `when`("toUUIDOrNull을 호출하면") {
            val result = uuid.toString().toUUIDOrNull()

            then("UUID로 변환되어야함") {
                result shouldBe uuid
            }
        }

        `when`("대문자 UUID 문자열로 toUUIDOrNull을 호출하면") {
            val result = uuid.toString().uppercase().toUUIDOrNull()

            then("UUID로 변환되어야함") {
                result shouldBe uuid
            }
        }
    }

    given("UUID.fromString이 허용하는 비표준 형식의 문자열이 주어지고") {
        val target = "1-2-3-4-5"

        `when`("toUUIDOrNull을 호출하면") {
            val result = target.toUUIDOrNull()

            then("null이 반환되어야함") {
                result shouldBe null
            }
        }
    }

    given("UUID 형식이 아닌 문자열이 주어지고") {
        val targets = listOf("", "invalid-id", "123e4567-e89b-12d3-a456-42661417400g", "123e4567e89b12d3a456426614174000")

        `when`("toUUIDOrNull을 호출하면") {
            val results = targets.map { it.toUUIDOrNull() }

            then("모두 null이 반환되어야함") {
                results.all { it == null } shouldBe true
            }
        }
    }
})
