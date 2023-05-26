package dev.rkashapov.testing.function

import dev.rkashapov.base.logging.MdcKey.*
import dev.rkashapov.base.logging.withLoggingContext
import dev.rkashapov.prs.testing.api.model.QuestionDifficulty
import dev.rkashapov.testing.entity.QuestionAnswerEntity
import mu.KLogging
import org.springframework.stereotype.Service
import java.util.function.Function

@Service
class NextDifficultySupplier : Function<QuestionAnswerEntity, QuestionDifficulty>, KLogging() {

    fun nextDifficulty(answer: QuestionAnswerEntity) = apply(answer = answer)

    override fun apply(answer: QuestionAnswerEntity): QuestionDifficulty {
        return withLoggingContext(
            TEST_ID to answer.session.sessionId,
            TEST_SESSION_ID to answer.session.sessionId,
            USER_ID to answer.session.respondent.id
        ) {
            logger.info { "Calculating next question difficulty" }
            answer.nextDifficulty().also {
                logger.debug { "Next question difficulty: $it" }
            }
        }
    }
}
