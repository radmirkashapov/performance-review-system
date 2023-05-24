package dev.rkashapov.testing.service

import dev.rkashapov.base.logging.MdcKey
import dev.rkashapov.base.logging.MdcKey.TEST_SESSION_ID
import dev.rkashapov.base.logging.withLoggingContext
import dev.rkashapov.prs.testing.api.model.TestSessionModel
import dev.rkashapov.prs.testing.api.model.TestSessionStatus.*
import dev.rkashapov.testing.converter.TestSessionEntity2ModelConverter
import dev.rkashapov.testing.entity.TestSessionEntity
import dev.rkashapov.testing.repository.TestRepository
import dev.rkashapov.testing.repository.TestSessionRepository
import dev.rkashapov.user.repository.UserRepository
import mu.KLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class TestSessionService(
    private val userRepository: UserRepository,
    private val testSessionRepository: TestSessionRepository,
    private val testSessionEntity2ModelConverter: TestSessionEntity2ModelConverter,
    private val testRepository: TestRepository
) : KLogging() {

    @Transactional
    fun startSession(testId: UUID, userId: UUID): TestSessionModel {
        return withLoggingContext(MdcKey.USER_ID to userId, MdcKey.TEST_ID to testId) {
            val user = userRepository.getReferenceById(userId)
            val test = testRepository.getReferenceById(testId)

            val existedSession = testSessionRepository
                .findFirstByRespondentAndTestAndStatusIn(user, test, listOf(ACTIVE, DELAYED))

            val testSession = if (existedSession != null) {
                withLoggingContext(TEST_SESSION_ID to existedSession.sessionId) {
                    logger.info { "Test session already exists" }

                    when (existedSession.status) {
                        ACTIVE -> {
                            logger.info { "Responding with existed session" }
                            return testSessionEntity2ModelConverter.convert(existedSession)
                        }

                        DELAYED -> existedSession.copy(status = ACTIVE)
                        FINISHED, FINISHED_BY_USER -> error("Illegal test session state")
                    }
                }
            } else {
                logger.info { "Test session doesn't exist. Creating..." }

                TestSessionEntity(
                    test = test,
                    respondent = user
                )
            }

            testSessionRepository.saveAndFlush(testSession).let {
                testSessionEntity2ModelConverter.convert(it)
            }
        }
    }

    @Transactional
    fun finishSession(sessionId: UUID) {
        withLoggingContext(TEST_SESSION_ID to sessionId) {
            val session = testSessionRepository.findById(sessionId).orElseThrow()

            logger.debug { "Found test session: $session" }

            withLoggingContext(MdcKey.TEST_ID to session.test.id) {
                logger.info { "Finishing test session..." }

                if (session.status != FINISHED_BY_USER && session.status != FINISHED) {
                    testSessionRepository.saveAndFlush(session.copy(status = FINISHED_BY_USER))
                }

                logger.info { "Test session finished" }

            }
        }
    }

}
