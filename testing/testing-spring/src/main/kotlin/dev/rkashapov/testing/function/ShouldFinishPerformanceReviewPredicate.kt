package dev.rkashapov.testing.function

import dev.rkashapov.base.logging.MdcKey.*
import dev.rkashapov.base.logging.withLoggingContext
import dev.rkashapov.testing.entity.QuestionAnswerEntity
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.function.BiPredicate

@Component
class ShouldFinishPerformanceReviewPredicate : BiPredicate<Set<String>, List<QuestionAnswerEntity>>, KLogging() {

    @Value("\${prs.max_questions_count:30}")
    private var maxQuestionCount: Int = 30

    override fun test(selectedSkills: Set<String>, answers: List<QuestionAnswerEntity>): Boolean {

        if (answers.isEmpty()) {
            return false
        }

        val session = answers.first().session

        return withLoggingContext(
            USER_ID to session.respondent.id,
            TEST_ID to session.test.id,
            TEST_SESSION_ID to session.sessionId)
        {
            logger.info { "Starting ShouldFinishPerformanceReviewPredicate..." }

            val answeredSkills = answers.map { it.question.skills.first().name }.toSet()

            if(selectedSkills.intersect(answeredSkills).size != selectedSkills.size) {
                return@withLoggingContext false
            }

            if (answers.size >= maxQuestionCount) {
                return@withLoggingContext true
            }

            return@withLoggingContext false
        }.also {
            logger.info { "ShouldFinishPerformanceReviewPredicate: $it" }
        }
    }

}
