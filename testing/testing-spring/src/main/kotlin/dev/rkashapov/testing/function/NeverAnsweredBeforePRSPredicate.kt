package dev.rkashapov.testing.function

import dev.rkashapov.base.logging.MdcKey.*
import dev.rkashapov.base.logging.withLoggingContext
import dev.rkashapov.testing.entity.TestSessionEntity
import dev.rkashapov.testing.repository.QuestionAnswerRepository
import mu.KLogging
import org.springframework.stereotype.Component
import java.util.function.Predicate


/**
 * Shows if user has never answered to any of skills during test in performance review process
 * */
@Component
class NeverAnsweredBeforePRSPredicate(
    private val questionAnswerRepository: QuestionAnswerRepository
) : Predicate<TestSessionEntity>, KLogging() {
    override fun test(session: TestSessionEntity): Boolean =
        withLoggingContext(
            TEST_ID to session.test.id,
            TEST_SESSION_ID to session.sessionId,
            USER_ID to session.respondent.id
        ) {

            logger.info { "Checking if respondent has never answered to any of test questions..." }

            return@withLoggingContext questionAnswerRepository
                .existsBySession(session)
                .not()
                .also { result ->
                    if (result) {
                        logger.info { "Answers not found" }
                    } else {
                        logger.info { "Answers exist" }
                    }
                }
        }

}
