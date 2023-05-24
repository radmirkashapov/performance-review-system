package dev.rkashapov.testing.service

import com.openai.client.api.client.QuestionOpenAIWebClient
import dev.rkashapov.base.logging.MdcKey
import dev.rkashapov.base.logging.withLoggingContext
import dev.rkashapov.prs.testing.api.model.QuestionDifficulty
import dev.rkashapov.prs.testing.api.model.TestCheckListQuestionOption
import dev.rkashapov.testing.converter.GeneratedQuestions2QuestionEntityConverter
import dev.rkashapov.testing.entity.QuestionEntity
import dev.rkashapov.testing.entity.SkillEntity
import dev.rkashapov.testing.repository.QuestionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.time.Duration.Companion.seconds

@Service
class ChatCompletionQuestionService(
    private val questionOpenAIWebClient: QuestionOpenAIWebClient,
    private val generatedQuestions2QuestionEntityConverter: GeneratedQuestions2QuestionEntityConverter,
    private val questionRepository: QuestionRepository
) : KLogging() {

    @Transactional
    fun retrieve(
        userId: UUID,
        testName: String,
        skill: SkillEntity,
        skillCheckListAnswer: Set<TestCheckListQuestionOption>,
        requestedDifficulties: Set<QuestionDifficulty>
    ): List<QuestionEntity> {
        withLoggingContext(
            MdcKey.USER_ID to userId,
        ) {

            val completion = retryRunBlocking(maxAttempts = 2, delayInSeconds = 1, nearActionWithDelay = false) {
                questionOpenAIWebClient
                    .getQuestionsWithDifficultyRange(
                        userId = userId,
                        testName = testName,
                        skillName = skill.name,
                        skillCheckListAnswer = skillCheckListAnswer,
                        requestedDifficulties = requestedDifficulties
                    )
            }

            val questions = generatedQuestions2QuestionEntityConverter.convert(completion, skill)

            logger.debug { "questions: $questions" }

            return questionRepository.saveAllAndFlush(questions)
        }
    }

}

fun <R> retryRunBlocking(
    maxAttempts: Int,
    delayInSeconds: Long,
    nearActionWithDelay: Boolean,
    action: suspend () -> R
): R {
    require(maxAttempts > 0) { "maxAttempts must be greater than 0" }
    return runCatching {
        runBlocking {
            if (nearActionWithDelay) {
                delay(delayInSeconds.seconds)
            }

            action()
        }
    }.getOrElse {
        val leftAttempts = maxAttempts.dec()
        if (leftAttempts == 0) throw it
        retryRunBlocking(maxAttempts = leftAttempts, delayInSeconds = delayInSeconds, nearActionWithDelay = true, action)
    }
}
