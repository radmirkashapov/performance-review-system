package dev.rkashapov.testing.repository

import dev.rkashapov.testing.entity.CheckListQuestionAnswerEntity
import dev.rkashapov.testing.entity.CheckListQuestionEntity
import dev.rkashapov.testing.entity.TestSessionEntity
import dev.rkashapov.user.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface CheckListQuestionAnswerRepository : JpaRepository<CheckListQuestionAnswerEntity, UUID> {
    fun findFirstByRespondentAndSessionAndQuestion(
        respondent: UserEntity,
        session: TestSessionEntity,
        question: CheckListQuestionEntity
    ): CheckListQuestionAnswerEntity?

    fun findAllBySession(session: TestSessionEntity): List<CheckListQuestionAnswerEntity>
}
