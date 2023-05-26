package dev.rkashapov.testing.service

import dev.rkashapov.base.logging.MdcKey.*
import dev.rkashapov.base.logging.withLoggingContext
import dev.rkashapov.prs.testing.api.model.TestCheckListDoAnswerResponse
import dev.rkashapov.prs.testing.api.model.TestCheckListQuestionOption
import dev.rkashapov.prs.testing.api.model.TestCheckListQuestionOptionRank
import dev.rkashapov.prs.testing.api.model.TestSessionStatus
import dev.rkashapov.testing.entity.CheckListQuestionAnswerEntity
import dev.rkashapov.testing.entity.TestSessionEntity
import dev.rkashapov.testing.repository.*
import dev.rkashapov.user.repository.UserRepository
import mu.KLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class TestCheckListAnswerService(
    private val testRepository: TestRepository,
    private val testSessionRepository: TestSessionRepository,
    private val userRepository: UserRepository,
    private val skillRepository: SkillRepository,
    private val checkListQuestionAnswerRepository: CheckListQuestionAnswerRepository,
    private val checkListQuestionRepository: CheckListQuestionRepository
) : KLogging() {

    @Transactional
    fun doAnswer(
        respondentId: UUID,
        skillName: String,
        testId: UUID,
        sessionId: UUID,
        questionId: UUID,
        answer: Set<TestCheckListQuestionOption>
    ) =
        withLoggingContext(
            TEST_ID to testId,
            USER_ID to respondentId,
            TEST_SESSION_ID to sessionId,
            TEST_CHECKLIST_QUESTION_ID to questionId
        ) {

            logger.info { "Received answer to checklist" }

            val respondent = userRepository.getReferenceById(respondentId)

            logger.debug { "Found user: $respondentId" }

            val test = testRepository.findById(testId).orElseThrow()

            withLoggingContext(TEST_CHECKLIST_ID to test.checkList?.id) {
                logger.debug { "Found test: $test" }

                val activeTestSession = testSessionRepository.findById(sessionId).orElseThrow()

                withLoggingContext(TEST_SESSION_ID to activeTestSession.sessionId) {
                    logger.debug { "Found test session: $activeTestSession" }

                    checkIfSessionDelayed(activeTestSession)

                    val question = checkListQuestionRepository.getReferenceById(questionId)

                    withLoggingContext(TEST_CHECKLIST_QUESTION_ID to questionId) {
                        val respondentAnswer = checkListQuestionAnswerRepository
                            .findFirstByRespondentAndSessionAndQuestion(
                                respondent = respondent,
                                session = activeTestSession,
                                question = question
                            )?.apply {
                                logger.info { "Respondent has already answered to question. Updating answer..." }
                                this.answer = answer
                            } ?: run {
                            logger.info { "Respondent hasn't answered to question yet. Saving answer..." }

                            val skill = skillRepository.getReferenceById(skillName)

                            CheckListQuestionAnswerEntity(
                                question = question,
                                answer = answer,
                                respondent = respondent,
                                session = activeTestSession,
                                skill = skill
                            )
                        }

                        checkListQuestionAnswerRepository.save(respondentAnswer)

                        logger.info { "Answer saved" }

                        if (answer.size == 1 && answer.first().rank == TestCheckListQuestionOptionRank.TRAINEE) {
                            TestCheckListDoAnswerResponse(skipSkill = true)
                        } else TestCheckListDoAnswerResponse(skipSkill = false)
                    }
                }
            }
        }

    private fun checkIfSessionDelayed(activeTestSession: TestSessionEntity) {
        withLoggingContext(
            USER_ID to activeTestSession.respondent.id,
            TEST_ID to activeTestSession.test.id,
            TEST_SESSION_ID to activeTestSession.sessionId,
            TEST_CHECKLIST_ID to activeTestSession.test.checkList?.id
        ) {

            logger.info { "Checking if test session was delayed..." }

            if (activeTestSession.status == TestSessionStatus.DELAYED) {
                logger.info { "Test session was delayed. Changing status to ${TestSessionStatus.ACTIVE}..." }

                activeTestSession.status = TestSessionStatus.ACTIVE

                testSessionRepository.save(activeTestSession)

                logger.info { "Test session is in ${TestSessionStatus.ACTIVE} status now" }
            }
        }
    }

}
