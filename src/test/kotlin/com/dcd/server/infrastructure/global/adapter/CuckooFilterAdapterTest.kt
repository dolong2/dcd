package com.dcd.server.infrastructure.global.adapter

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.redisson.api.RedissonClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.UUID

@SpringBootTest
@ActiveProfiles("test")
class CuckooFilterAdapterTest(
    private val cuckooFilterAdapter: CuckooFilterAdapter,
    private val redissonClient: RedissonClient
) : BehaviorSpec({

    val filterNamesToCleanup = mutableListOf<String>()

    afterEach {
        filterNamesToCleanup.forEach { filterName ->
            try {
                redissonClient.getCuckooFilter<String>(filterName).delete()
            } catch (e: Exception) {
                // 필터가 없는 경우 무시
            }
        }
        filterNamesToCleanup.clear()
    }

    given("Cuckoo filter add 동작") {

        `when`("필터가 존재하지 않으면 init 호출 후 add 성공") {

            then("true를 반환해야 한다") {
                val filterName = "test-filter-${UUID.randomUUID()}"
                filterNamesToCleanup.add(filterName)
                val item = "item"

                val result = cuckooFilterAdapter.add(filterName, item)

                result shouldBe true

                // 필터가 생성되었는지 확인
                val cuckooFilter = redissonClient.getCuckooFilter<String>(filterName)
                cuckooFilter.isExists shouldBe true
                cuckooFilter.exists(item) shouldBe true
            }
        }

        `when`("필터가 이미 존재하고 같은 아이템을 다시 추가하면") {

            then("첫 번째는 true, 두 번째는 false를 반환해야 한다") {
                val filterName = "test-filter-${UUID.randomUUID()}"
                filterNamesToCleanup.add(filterName)
                val item = "item"

                val firstAdd = cuckooFilterAdapter.add(filterName, item)
                val secondAdd = cuckooFilterAdapter.add(filterName, item)

                firstAdd shouldBe true
                secondAdd shouldBe false
            }
        }

        `when`("필터에 여러 아이템을 추가하면") {

            then("모두 true를 반환해야 한다") {
                val filterName = "test-filter-${UUID.randomUUID()}"
                filterNamesToCleanup.add(filterName)
                val item1 = "item1"
                val item2 = "item2"

                val result1 = cuckooFilterAdapter.add(filterName, item1)
                val result2 = cuckooFilterAdapter.add(filterName, item2)

                result1 shouldBe true
                result2 shouldBe true

                val cuckooFilter = redissonClient.getCuckooFilter<String>(filterName)
                cuckooFilter.exists(item1) shouldBe true
                cuckooFilter.exists(item2) shouldBe true
            }
        }
    }

    given("Cuckoo filter remove 동작") {

        `when`("존재하는 아이템을 삭제하면") {

            then("true를 반환해야 한다") {
                val filterName = "test-filter-${UUID.randomUUID()}"
                filterNamesToCleanup.add(filterName)
                val item = "item"

                cuckooFilterAdapter.add(filterName, item)
                val result = cuckooFilterAdapter.remove(filterName, item)

                result shouldBe true

                val cuckooFilter = redissonClient.getCuckooFilter<String>(filterName)
                cuckooFilter.exists(item) shouldBe false
            }
        }

        `when`("존재하지 않는 아이템을 삭제하면") {

            then("false를 반환해야 한다") {
                val filterName = "test-filter-${UUID.randomUUID()}"
                filterNamesToCleanup.add(filterName)
                val item = "non-existent-item"

                cuckooFilterAdapter.add(filterName, "other-item")
                val result = cuckooFilterAdapter.remove(filterName, item)

                result shouldBe false
            }
        }

        `when`("필터가 존재하지 않는 상태에서 삭제하면") {
            then("true를 반환해야 한다") {
                val filterName = "test-filter-${UUID.randomUUID()}"
                filterNamesToCleanup.add(filterName)
                val item = "item"

                val result = cuckooFilterAdapter.remove(filterName, item)

                result shouldBe true
            }
        }
    }

    given("Cuckoo filter exists 동작") {

        `when`("존재하는 아이템을 확인하면") {
            then("true를 반환해야 한다") {
                val filterName = "test-filter-${UUID.randomUUID()}"
                filterNamesToCleanup.add(filterName)
                val item = "item"

                cuckooFilterAdapter.add(filterName, item)
                val result = cuckooFilterAdapter.exists(filterName, item)

                result shouldBe true
            }
        }

        `when`("존재하지 않는 아이템을 확인하면") {
            then("false를 반환해야 한다") {
                val filterName = "test-filter-${UUID.randomUUID()}"
                filterNamesToCleanup.add(filterName)
                val item = "non-existent-item"

                cuckooFilterAdapter.add(filterName, "other-item")
                val result = cuckooFilterAdapter.exists(filterName, item)

                result shouldBe false
            }
        }

        `when`("필터가 존재하지 않는 상태에서 확인하면") {
            then("false를 반환해야 한다") {
                val filterName = "test-filter-${UUID.randomUUID()}"
                filterNamesToCleanup.add(filterName)
                val item = "item"

                val result = cuckooFilterAdapter.exists(filterName, item)

                result shouldBe false
            }
        }

        `when`("아이템 추가 후 삭제하고 확인하면") {
            then("false를 반환해야 한다") {
                val filterName = "test-filter-${UUID.randomUUID()}"
                filterNamesToCleanup.add(filterName)
                val item = "item"

                cuckooFilterAdapter.add(filterName, item)
                cuckooFilterAdapter.remove(filterName, item)
                val result = cuckooFilterAdapter.exists(filterName, item)

                result shouldBe false
            }
        }
    }
})
