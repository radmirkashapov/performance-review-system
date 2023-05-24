package dev.rkashapov.testing.repository

import dev.rkashapov.testing.entity.QuestionAnswerEntity
import dev.rkashapov.testing.entity.TestSessionEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface QuestionAnswerRepository : JpaRepository<QuestionAnswerEntity, UUID> {
    fun existsBySession(session: TestSessionEntity): Boolean

    fun findAllBySessionOrderByCreatedAt(session: TestSessionEntity): List<QuestionAnswerEntity>

}
