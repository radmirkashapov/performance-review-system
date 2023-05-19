package dev.rkashapov.testing.repository

import dev.rkashapov.prs.testing.api.model.TestSessionStatus
import dev.rkashapov.testing.entity.TestEntity
import dev.rkashapov.testing.entity.TestSessionEntity
import dev.rkashapov.user.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TestSessionRepository : JpaRepository<TestSessionEntity, UUID> {
    fun findFirstByRespondentAndTestAndStatusIn(
        respondent: UserEntity,
        test: TestEntity,
        statuses: List<TestSessionStatus>
    ): TestSessionEntity?
}
