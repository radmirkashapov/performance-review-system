package dev.rkashapov.testing.repository

import dev.rkashapov.testing.entity.TestCheckListEntity
import dev.rkashapov.testing.entity.TestEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TestCheckListRepository : JpaRepository<TestCheckListEntity, UUID> {
    fun findByTest(test: TestEntity): TestCheckListEntity?
}
