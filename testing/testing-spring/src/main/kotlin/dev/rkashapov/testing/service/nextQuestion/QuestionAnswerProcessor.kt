package dev.rkashapov.testing.service.nextQuestion

import dev.rkashapov.prs.testing.api.model.NextQuestion
import dev.rkashapov.prs.testing.api.model.NextQuestionProcessorType
import dev.rkashapov.prs.testing.api.model.PerformanceReviewDoAnswerRequest
import dev.rkashapov.testing.entity.TestSessionEntity
import dev.rkashapov.user.entity.UserEntity
import java.util.*

interface QuestionAnswerProcessor {
    fun supports(): NextQuestionProcessorType
    fun process(
        testId: UUID,
        respondent: UserEntity,
        session: TestSessionEntity,
        answer: PerformanceReviewDoAnswerRequest? = null
    ): NextQuestion
}
