package com.dcd.server.core.common.service

import com.dcd.server.core.common.service.exception.BloomFilterReservationException
import com.dcd.server.core.common.service.impl.CuckooFilterServiceImpl
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.connection.RedisCommands
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class CuckooFilterServiceImplTest(
    rawTemplate: StringRedisTemplate
) : BehaviorSpec({
    val redisTemplate = spyk(rawTemplate)
    val cuckooFilterService = CuckooFilterServiceImpl(redisTemplate)
    val redisConnection = spyk(redisTemplate.getConnectionFactory()!!.getConnection())
    val redisCommands = spyk(redisConnection.commands())

    beforeSpec {
        // redisTemplate.execute() 호출 시 콜백을 직접 실행하도록 설정
        every { redisTemplate.execute(any<RedisCallback<Any>>()) } answers {
            val callback = firstArg<RedisCallback<Any>>()
            callback.doInRedis(redisConnection)
        }
        // redisConnection에서 commands() 호출 시 redisCommands 반환
        every { redisConnection.commands() } returns redisCommands
    }

    given("Cuckoo filter add 동작") {

        `when`("필터가 존재하지 않으면 CF.RESERVE 호출 후 CF.ADDNX 성공") {
            val filterName = "test-filter"
            val item = "item"

            every { redisTemplate.hasKey(filterName) } returns false
            every {
                redisCommands.execute("CF.RESERVE", filterName.toByteArray(), "100000".toByteArray())
            } returns "OK"
            every {
                redisCommands.execute("CF.ADDNX", filterName.toByteArray(), item.toByteArray())
            } returns 1L

            then("true를 반환하고, CF.RESERVE와 CF.ADDNX가 호출되어야 한다") {
                val result = cuckooFilterService.add(filterName, item)
                
                result shouldBe true
                verify(exactly = 1) { redisTemplate.hasKey(filterName) }
                verify(exactly = 1) { redisCommands.execute("CF.RESERVE", filterName.toByteArray(), "100000".toByteArray()) }
                verify(exactly = 1) { redisCommands.execute("CF.ADDNX", filterName.toByteArray(), item.toByteArray()) }
            }
        }

        `when`("필터가 이미 존재하면 CF.RESERVE를 생략하고 CF.ADDNX만 호출") {
            val filterName = "test-filter"
            val item = "item"

            clearMocks(redisTemplate, redisCommands, answers = false)
            every { redisTemplate.hasKey(filterName) } returns true
            every {
                redisCommands.execute("CF.ADDNX", filterName.toByteArray(), item.toByteArray())
            } returns 0L

            then("false를 반환하고, CF.RESERVE는 호출되지 않아야 한다") {
                val result = cuckooFilterService.add(filterName, item)
                
                result shouldBe false
                verify(exactly = 1) { redisTemplate.hasKey(filterName) }
                verify(exactly = 0) { redisCommands.execute("CF.RESERVE", any(), any()) }
                verify(exactly = 1) { redisCommands.execute("CF.ADDNX", filterName.toByteArray(), item.toByteArray()) }
            }
        }

        `when`("필터 예약(CF.RESERVE) 실패 시 예외 발생") {
            val filterName = "test-filter"
            val item = "item"

            clearMocks(redisTemplate, redisCommands, answers = false)
            every { redisTemplate.hasKey(filterName) } returns false
            every {
                redisCommands.execute("CF.RESERVE", filterName.toByteArray(), "100000".toByteArray())
            } returns "ERROR"

            then("BloomFilterReservationException 예외가 발생해야 하고, CF.ADDNX는 호출되지 않아야 한다") {
                shouldThrow<BloomFilterReservationException> {
                    cuckooFilterService.add(filterName, item)
                }
                
                verify(exactly = 1) { redisTemplate.hasKey(filterName) }
                verify(exactly = 1) { redisCommands.execute("CF.RESERVE", filterName.toByteArray(), "100000".toByteArray()) }
                verify(exactly = 0) { redisCommands.execute("CF.ADDNX", any(), any()) }
            }
        }
    }

    given("Cuckoo filter remove 동작") {

        `when`("CF.DEL이 1을 반환하면 true") {
            val filterName = "test-filter"
            val item = "item"

            clearMocks(redisTemplate, redisCommands, answers = false)
            every {
                redisCommands.execute("CF.DEL", filterName.toByteArray(), item.toByteArray())
            } returns 1L

            then("true를 반환하고, CF.DEL이 호출되어야 한다") {
                val result = cuckooFilterService.remove(filterName, item)
                
                result shouldBe true
                verify(exactly = 1) { redisCommands.execute("CF.DEL", filterName.toByteArray(), item.toByteArray()) }
            }
        }

        `when`("CF.DEL이 0을 반환하면 false") {
            val filterName = "test-filter"
            val item = "item"

            clearMocks(redisTemplate, redisCommands, answers = false)
            every {
                redisCommands.execute("CF.DEL", filterName.toByteArray(), item.toByteArray())
            } returns 0L

            then("false를 반환해야 한다") {
                val result = cuckooFilterService.remove(filterName, item)
                
                result shouldBe false
                verify(exactly = 1) { redisCommands.execute("CF.DEL", filterName.toByteArray(), item.toByteArray()) }
            }
        }
    }

    given("Cuckoo filter exists 동작") {

        `when`("CF.EXISTS가 1을 반환하면 true") {
            val filterName = "test-filter"
            val item = "item"

            clearMocks(redisTemplate, redisCommands, answers = false)
            every {
                redisCommands.execute("CF.EXISTS", filterName.toByteArray(), item.toByteArray())
            } returns 1L

            then("true를 반환해야 한다") {
                val result = cuckooFilterService.exists(filterName, item)
                
                result shouldBe true
                verify(exactly = 1) { redisCommands.execute("CF.EXISTS", filterName.toByteArray(), item.toByteArray()) }
            }
        }

        `when`("CF.EXISTS가 0을 반환하면 false") {
            val filterName = "test-filter"
            val item = "item"

            clearMocks(redisTemplate, redisCommands, answers = false)
            every {
                redisCommands.execute("CF.EXISTS", filterName.toByteArray(), item.toByteArray())
            } returns 0L

            then("false를 반환해야 한다") {
                val result = cuckooFilterService.exists(filterName, item)
                
                result shouldBe false
                verify(exactly = 1) { redisCommands.execute("CF.EXISTS", filterName.toByteArray(), item.toByteArray()) }
            }
        }
    }
})