package dev.rkashapov.testing.service.nextQuestion

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.map.IMap
import dev.rkashapov.base.caching.CollectionName
import dev.rkashapov.base.logging.MdcKey.*
import dev.rkashapov.base.logging.withLoggingContext
import dev.rkashapov.prs.testing.api.model.NextQuestion
import dev.rkashapov.prs.testing.api.model.NextQuestionProcessorType.NEXT_QUESTION
import dev.rkashapov.prs.testing.api.model.PerformanceReviewDoAnswerFinishTestResponse
import dev.rkashapov.prs.testing.api.model.PerformanceReviewDoAnswerRequest
import dev.rkashapov.prs.testing.api.model.TestSessionStatus
import dev.rkashapov.testing.entity.PRSStateTestHZModel
import dev.rkashapov.testing.entity.QuestionAnswerEntity
import dev.rkashapov.testing.entity.TestSessionEntity
import dev.rkashapov.testing.function.NextDifficultySupplier
import dev.rkashapov.testing.function.NextQuestionSupplier
import dev.rkashapov.testing.function.NextTopicSupplier
import dev.rkashapov.testing.function.ShouldFinishPerformanceReviewPredicate
import dev.rkashapov.testing.repository.QuestionAnswerRepository
import dev.rkashapov.testing.repository.QuestionRepository
import dev.rkashapov.testing.repository.SkillRepository
import dev.rkashapov.testing.repository.TestSessionRepository
import dev.rkashapov.user.entity.UserEntity
import mu.KLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class NextQuestionAnswerProcessor(
    private val nextTopicSupplier: NextTopicSupplier,
    private val shouldFinishPerformanceReviewPredicate: ShouldFinishPerformanceReviewPredicate,
    private val testSessionRepository: TestSessionRepository,
    private val questionAnswerRepository: QuestionAnswerRepository,
    private val questionRepository: QuestionRepository,
    private val hazelcastInstance: HazelcastInstance,
    private val nextDifficultySupplier: NextDifficultySupplier,
    private val nextQuestionSupplier: NextQuestionSupplier,
    private val skillRepository: SkillRepository
) : QuestionAnswerProcessor, KLogging() {

    // test session identifier to state model
    private val prsTestStates: IMap<UUID, PRSStateTestHZModel> =
        hazelcastInstance.getMap(CollectionName.prsTestStatesMap)

    override fun supports() = NEXT_QUESTION

    @Transactional
    override fun process(
        testId: UUID,
        respondent: UserEntity,
        session: TestSessionEntity,
        answer: PerformanceReviewDoAnswerRequest?
    ): NextQuestion {

        val sessionId = checkNotNull(session.sessionId)

        return withLoggingContext(
            TEST_SESSION_ID to session.sessionId,
            USER_ID to respondent.id,
            TEST_ID to testId,
            TEST_QUESTION_ID to answer?.questionId
        ) {
            logger.info { "NextQuestionAnswerProcessor: processing..." }

            requireNotNull(answer) { "Answer must be provided" }

            val alreadyAnsweredQuestions = questionAnswerRepository
                .findAllBySessionOrderByCreatedAt(session)

            val newAnswer = if (alreadyAnsweredQuestions.any { it.question.id == answer.questionId }) {
                logger.info { "Already answered" }
                alreadyAnsweredQuestions.last()
            } else {
                val question = questionRepository.getReferenceById(answer.questionId)

                questionAnswerRepository.save(
                    QuestionAnswerEntity(
                        question = question,
                        answer = answer.answer,
                        session = session
                    )
                ).also { newAnswer ->
                    logger.debug { "Saved question answer: $newAnswer" }
                }
            }

            val alreadyAnsweredQuestionsIds = alreadyAnsweredQuestions.mapNotNull { it.question.id }

            logger.debug { "alreadyAnsweredQuestionsIds: $alreadyAnsweredQuestionsIds" }


            val state = prsTestStates.getValue(sessionId)

            getNextQuestion(session, state, newAnswer, alreadyAnsweredQuestions.toSet()).also {
                logger.debug { "NextQuestionAnswerProcessor answer: $it" }
            }
        }
    }

    @Transactional
    fun getNextQuestion(
        session: TestSessionEntity,
        state: PRSStateTestHZModel,
        newAnswer: QuestionAnswerEntity,
        alreadyAnsweredQuestions: Set<QuestionAnswerEntity>
    ): NextQuestion {
        return withLoggingContext(
            TEST_SESSION_ID to session.sessionId,
            USER_ID to session.respondent.id,
            TEST_ID to session.test.id,
            TEST_QUESTION_ID to newAnswer.question.id
        ) {
            logger.info { "Getting next question..." }

            val respondentAnswers = alreadyAnsweredQuestions.plus(newAnswer)
            val nextDifficulty = nextDifficultySupplier.nextDifficulty(answer = respondentAnswers.last())

            // fixme
            val respondentAnswersAsList = respondentAnswers.toList()

            if (shouldFinishPerformanceReviewPredicate.test(
                    selectedSkills = state.checkListAnswersPerSkill.keys,
                    answers = respondentAnswersAsList
                )
            ) {
                logger.info { "Finishing test. nextQuestionDifficulty: $nextDifficulty" }
                testSessionRepository.save(session.copy(status = TestSessionStatus.FINISHED))

                return PerformanceReviewDoAnswerFinishTestResponse(nextQuestionDifficulty = nextDifficulty)
            }

            // topic == skill
            val nextTopic =
                nextTopicSupplier
                    .nextTopic(state = state, answers = respondentAnswersAsList)


            nextQuestionSupplier.getNextQuestion(
                session = session,
                alreadyAnsweredQuestionsIds = respondentAnswers.mapNotNull { it.question.id },
                skill = skillRepository.findById(nextTopic).orElseThrow(),
                requestedDifficulty = nextDifficulty,
                checkListAnswersPerSkill = state.checkListAnswersPerSkill
            )
        }
    }
}
