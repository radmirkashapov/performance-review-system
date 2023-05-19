package dev.rkashapov.testing.repository

import dev.rkashapov.testing.entity.TestEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TestRepository : JpaRepository<TestEntity, UUID> {
}
