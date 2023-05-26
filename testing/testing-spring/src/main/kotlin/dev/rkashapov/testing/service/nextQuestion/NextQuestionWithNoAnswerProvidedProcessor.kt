package dev.rkashapov.testing.service.nextQuestion

import dev.rkashapov.base.logging.MdcKey.*
import dev.rkashapov.base.logging.withLoggingContext
import dev.rkashapov.prs.testing.api.model.NextQuestion
import dev.rkashapov.prs.testing.api.model.NextQuestionProcessorType.NEXT_QUESTION_WITH_NO_ANSWER_PROVIDED
import dev.rkashapov.prs.testing.api.model.PerformanceReviewDoAnswerRequest
import dev.rkashapov.testing.entity.TestSessionEntity
import dev.rkashapov.testing.repository.CheckListQuestionAnswerRepository
import dev.rkashapov.testing.repository.QuestionAnswerRepository
import dev.rkashapov.user.entity.UserEntity
import mu.KLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class NextQuestionWithNoAnswerProvidedProcessor(
    private val firstQuestionAnswerProcessor: FirstQuestionAnswerProcessor,
    private val nextQuestionAnswerProcessor: NextQuestionAnswerProcessor,
    private val checkListQuestionAnswerRepository: CheckListQuestionAnswerRepository,
    private val questionAnswerRepository: QuestionAnswerRepository
) : QuestionAnswerProcessor, KLogging() {
    override fun supports() = NEXT_QUESTION_WITH_NO_ANSWER_PROVIDED

    @Transactional
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

            logger.info { "NextQuestionWithNoAnswerProvidedProcessor: processing..." }

            val checkListAnswers = checkListQuestionAnswerRepository.findAllBySession(session)
            logger.info { "Found checkListAnswers: size ${checkListAnswers.size}" }

            val state = firstQuestionAnswerProcessor.initPRSState(
                session = session,
                respondent = respondent,
                testId = testId,
                checkListAnswers = checkListAnswers
            )

            val answeredQuestions = questionAnswerRepository.findAllBySessionOrderByCreatedAt(session)

            return nextQuestionAnswerProcessor
                .getNextQuestion(session, state, answeredQuestions.last(), answeredQuestions)
                .also {
                    logger.debug { "NextQuestionWithNoAnswerProvidedProcessor answer: $it" }
                }

        }
    }
}
