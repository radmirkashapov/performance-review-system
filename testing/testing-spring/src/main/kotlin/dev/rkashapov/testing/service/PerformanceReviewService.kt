package dev.rkashapov.testing.service

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.map.IMap
import dev.rkashapov.base.caching.CollectionName
import dev.rkashapov.base.logging.MdcKey.*
import dev.rkashapov.base.logging.withLoggingContext
import dev.rkashapov.prs.testing.api.model.NextQuestion
import dev.rkashapov.prs.testing.api.model.NextQuestionProcessorType.*
import dev.rkashapov.prs.testing.api.model.PerformanceReviewDoAnswerRequest
import dev.rkashapov.prs.testing.api.model.TestSessionStatus
import dev.rkashapov.prs.testing.api.model.TestSessionStatus.DELAYED
import dev.rkashapov.testing.converter.QuestionEntity2NextQuestionModelConverter
import dev.rkashapov.testing.entity.PRSStateTestHZModel
import dev.rkashapov.testing.function.NeverAnsweredBeforePRSPredicate
import dev.rkashapov.testing.repository.QuestionRepository
import dev.rkashapov.testing.repository.TestRepository
import dev.rkashapov.testing.repository.TestSessionRepository
import dev.rkashapov.testing.service.nextQuestion.QuestionAnswerProcessor
import dev.rkashapov.user.repository.UserRepository
import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class PerformanceReviewService(
    private val neverAnsweredBeforePRSPredicate: NeverAnsweredBeforePRSPredicate,
    private val userRepository: UserRepository,
    private val testRepository: TestRepository,
    private val testSessionRepository: TestSessionRepository,
    private val questionAnswerProcessors: Array<QuestionAnswerProcessor>,
    private val hazelcastInstance: HazelcastInstance,
    private val questionEntity2NextQuestionModelConverter: QuestionEntity2NextQuestionModelConverter,
    private val questionRepository: QuestionRepository
) : KLogging() {

    // test session identifier to state model
    private val prsTestStates: IMap<UUID, PRSStateTestHZModel> = hazelcastInstance.getMap(CollectionName.prsTestStatesMap)

    /**
     * Consumes answer and returns next question (or inform about end of test)
     * If there is no answers, returns first question
     * */
    @Transactional
    fun doAnswer(testId: UUID, userId: UUID, answer: PerformanceReviewDoAnswerRequest? = null): NextQuestion {
        withLoggingContext(USER_ID to userId, TEST_ID to testId) {
            logger.info { "Received answer request during performance review process" }

            val user = userRepository.getReferenceById(userId)
            val test = testRepository.getReferenceById(testId)

            val session = testSessionRepository
                .findFirstByRespondentAndTestAndStatusIn(user, test, listOf(TestSessionStatus.ACTIVE, DELAYED))
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Session must be started")

            withLoggingContext(TEST_SESSION_ID to session.sessionId) {
                logger.debug { "Found session: $session" }

                val questionProcessor = if (answer == null) {
                    val neverAnsweredBefore = neverAnsweredBeforePRSPredicate.test(session)
                    if (neverAnsweredBefore) {
                        questionAnswerProcessors.first { it.supports() == FIRST_QUESTION }
                    } else questionAnswerProcessors.first { it.supports() == NEXT_QUESTION_WITH_NO_ANSWER_PROVIDED }
                } else {
                    questionAnswerProcessors.first { it.supports() == NEXT_QUESTION }
                }

                return questionProcessor.process(
                    testId = testId,
                    respondent = user,
                    session = session,
                    answer = answer
                )
            }
        }
    }

}
