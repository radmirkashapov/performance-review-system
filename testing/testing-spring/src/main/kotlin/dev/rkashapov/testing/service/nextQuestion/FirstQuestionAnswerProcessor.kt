package dev.rkashapov.testing.service.nextQuestion

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.map.IMap
import dev.rkashapov.base.caching.CollectionName
import dev.rkashapov.base.logging.MdcKey.*
import dev.rkashapov.base.logging.withLoggingContext
import dev.rkashapov.prs.testing.api.model.NextQuestion
import dev.rkashapov.prs.testing.api.model.NextQuestionProcessorType
import dev.rkashapov.prs.testing.api.model.PerformanceReviewDoAnswerRequest
import dev.rkashapov.prs.testing.api.model.QuestionDifficulty.MIDDLE
import dev.rkashapov.prs.testing.api.model.TestCheckListQuestionOptionRank.TRAINEE
import dev.rkashapov.testing.entity.CheckListQuestionAnswerEntity
import dev.rkashapov.testing.entity.PRSStateTestHZModel
import dev.rkashapov.testing.entity.TestSessionEntity
import dev.rkashapov.testing.function.NextQuestionSupplier
import dev.rkashapov.testing.repository.CheckListQuestionAnswerRepository
import dev.rkashapov.testing.repository.QuestionAnswerRepository
import dev.rkashapov.testing.service.UserTopicMatrixConnectivityMockService
import dev.rkashapov.user.entity.UserEntity
import mu.KLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class FirstQuestionAnswerProcessor(
    private val checkListQuestionAnswerRepository: CheckListQuestionAnswerRepository,
    private val userTopicMatrixConnectivityMockService: UserTopicMatrixConnectivityMockService,
    private val questionAnswerRepository: QuestionAnswerRepository,
    private val nextQuestionSupplier: NextQuestionSupplier,
    private val hazelcastInstance: HazelcastInstance
) : QuestionAnswerProcessor, KLogging() {

    // test session identifier to state model
    private val prsTestStates: IMap<UUID, PRSStateTestHZModel> = hazelcastInstance.getMap(CollectionName.prsTestStatesMap)

    override fun supports() = NextQuestionProcessorType.FIRST_QUESTION


    @Transactional
    // FIXME too complicated
    override fun process(
        testId: UUID,
        respondent: UserEntity,
        session: TestSessionEntity,
        answer: PerformanceReviewDoAnswerRequest?
    ): NextQuestion {
        withLoggingContext(
            TEST_SESSION_ID to session.sessionId,
            USER_ID to respondent.id,
            TEST_ID to session.test.id
        ) {

            logger.info { "FirstQuestionAnswerProcessor: processing..." }

            val alreadyAnsweredQuestions = questionAnswerRepository
                .findAllBySessionOrderByCreatedAt(session)

            val alreadyAnsweredQuestionsIds = alreadyAnsweredQuestions.mapNotNull { it.question.id }

            logger.debug { "alreadyAnsweredQuestionsIds: $alreadyAnsweredQuestionsIds" }

            val checkListAnswers = checkListQuestionAnswerRepository.findAllBySession(session)
            logger.info { "Found checkListAnswers: size ${checkListAnswers.size}" }

            val state = initPRSState(session, respondent, testId, checkListAnswers)

            val randomSkill = checkListAnswers.filter { answer -> !answer.answer.any { answerOption -> answerOption.rank == TRAINEE } }.random().skill

            return nextQuestionSupplier.getNextQuestion(
                session = session,
                skill = randomSkill,
                alreadyAnsweredQuestionsIds = alreadyAnsweredQuestionsIds,
                checkListAnswersPerSkill = state.checkListAnswersPerSkill,
                requestedDifficulty = MIDDLE
            ).also {
                logger.debug { "FirstQuestionAnswerProcessor answer: $it" }
            }
        }
    }

    @Transactional
    fun initPRSState(
        session: TestSessionEntity,
        respondent: UserEntity,
        testId: UUID,
        checkListAnswers: List<CheckListQuestionAnswerEntity>
    ): PRSStateTestHZModel {
        withLoggingContext(
            TEST_SESSION_ID to session.sessionId,
            USER_ID to respondent.id,
            TEST_ID to session.test.id
        ) {
            val checkListAnswersPerSkill = checkListAnswers
                .asSequence()
                .filter { answer -> !answer.answer.any { option -> option.rank == TRAINEE } }
                .groupBy({ answer -> answer.skill.name }) { answer -> answer.answer }
                .mapValues { option -> option.value.flatten() }

            val state = PRSStateTestHZModel(
                userId = checkNotNull(respondent.id),
                testId = testId,
                checkListAnswersPerSkill = checkListAnswersPerSkill,
                topicMatrixConnectivity = userTopicMatrixConnectivityMockService
                    .getMatrixByUsersSkills(
                        testId = testId,
                        selectedSkills = checkListAnswersPerSkill.keys.toList()
                    )
            )

            prsTestStates.put(
                checkNotNull(session.sessionId),
                state
            )

            logger.debug { "Updated prs test session state: $state" }
            return state
        }
    }
}
