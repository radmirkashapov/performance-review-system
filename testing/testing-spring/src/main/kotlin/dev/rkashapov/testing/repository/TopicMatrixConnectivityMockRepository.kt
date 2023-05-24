package dev.rkashapov.testing.repository

import dev.rkashapov.testing.entity.TestEntity
import dev.rkashapov.testing.entity.TopicMatrixConnectivityMock
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TopicMatrixConnectivityMockRepository : JpaRepository<TopicMatrixConnectivityMock, UUID> {
    fun findByTest(test: TestEntity): TopicMatrixConnectivityMock?
}
