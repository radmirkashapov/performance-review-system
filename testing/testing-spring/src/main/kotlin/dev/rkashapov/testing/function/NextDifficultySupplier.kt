package dev.rkashapov.testing.function

import dev.rkashapov.base.logging.MdcKey.*
import dev.rkashapov.base.logging.withLoggingContext
import dev.rkashapov.prs.testing.api.model.QuestionAnswerType.ANSWER_BY_PROBLEM
import dev.rkashapov.prs.testing.api.model.QuestionAnswerType.ANSWER_BY_QUESTION
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

            when {
                answer.answer[ANSWER_BY_PROBLEM]?.isNotEmpty() == true -> answer.question.difficulty.nextOrCurrentIfNotExists()
                answer.answer.containsKey(ANSWER_BY_QUESTION) -> {
                    val userAnswer = answer.answer.getValue(ANSWER_BY_QUESTION)

                    answer.question.correctAnswers.size.takeIf { it != 0 }?.let {
                        val rank =
                            userAnswer.intersect(answer.question.correctAnswers).size.toDouble() / answer.question.correctAnswers.size
                        if (rank >= 0.85) {
                            answer.question.difficulty.nextOrCurrentIfNotExists()
                        } else {
                            answer.question.difficulty.previousOrCurrentIfNotExists()
                        }
                    } ?: run {
                        if (answer.question.correctAnswers.isEmpty() && userAnswer.isEmpty()) {
                            answer.question.difficulty.nextOrCurrentIfNotExists()
                        } else answer.question.difficulty.previousOrCurrentIfNotExists()
                    }
                }
                answer.answer.isEmpty() -> {
                    if (answer.question.correctAnswers.isEmpty()) {
                        answer.question.difficulty.nextOrCurrentIfNotExists()
                    } else answer.question.difficulty.previousOrCurrentIfNotExists()
                }
                else -> throw NotImplementedError("Unsupported answers")
            }.also {
                logger.debug { "Next question difficulty: $it" }
            }
        }
    }
}
