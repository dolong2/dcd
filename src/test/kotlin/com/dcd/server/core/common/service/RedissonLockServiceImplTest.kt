package com.dcd.server.core.common.service

import com.dcd.server.core.common.service.impl.RedissonLockServiceImpl
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.Runs
import io.mockk.verify
import org.redisson.api.RLock
import org.redisson.api.RedissonClient

class RedissonLockServiceImplTest : BehaviorSpec({

    val redissonClient = mockk<RedissonClient>()
    val lock = mockk<RLock>()

    lateinit var lockService: RedissonLockServiceImpl

    beforeTest {
        clearAllMocks()
        every { redissonClient.getLock(any() as String) } returns lock
        lockService = RedissonLockServiceImpl(redissonClient)
    }

    Given("락 획득이 성공했을 때") {

        every { lock.tryLock(any(), any(), any()) } returns true
        every { lock.isHeldByCurrentThread } returns true
        every { lock.unlock() } just Runs

        When("block을 실행하면") {

            val result = lockService.lock("test-lock", 1000, 3000) {
                "success"
            }

            Then("block 결과를 반환한다") {
                result shouldBe "success"
            }

            Then("unlock이 호출된다") {
                verify { lock.unlock() }
            }
        }
    }

    Given("락 획득이 실패했을 때") {

        every { lock.tryLock(any(), any(), any()) } returns false

        When("lock을 실행하면") {

            val result = lockService.lock("test-lock", 1000, 3000) {
                "should not run"
            }

            Then("null을 반환한다") {
                result shouldBe null
            }

            Then("unlock은 호출되지 않는다") {
                verify(exactly = 0) { lock.unlock() }
            }
        }
    }

    Given("block 실행 중 예외가 발생하면") {

        every { lock.tryLock(any(), any(), any()) } returns true
        every { lock.isHeldByCurrentThread } returns true
        every { lock.unlock() } just Runs

        When("block에서 exception이 발생하면") {

            Then("unlock은 반드시 호출된다") {

                shouldThrow<RuntimeException> {
                    lockService.lock("test-lock", 1000, 3000) {
                        throw RuntimeException("error")
                    }
                }

                verify { lock.unlock() }
            }
        }
    }
})