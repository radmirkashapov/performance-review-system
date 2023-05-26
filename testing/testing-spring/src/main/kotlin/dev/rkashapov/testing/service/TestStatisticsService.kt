package dev.rkashapov.testing.service

import dev.rkashapov.base.logging.MdcKey.TEST_SESSION_ID
import dev.rkashapov.base.logging.MdcKey.USER_ID
import dev.rkashapov.base.logging.withLoggingContext
import dev.rkashapov.prs.testing.api.model.ExtendedTestStatisticsModel
import dev.rkashapov.prs.testing.api.model.NameDifficultyData
import dev.rkashapov.prs.testing.api.model.NameValueData
import dev.rkashapov.prs.testing.api.model.SimpleTestStatisticsModel
import dev.rkashapov.prs.testing.api.model.TestSessionStatus.FINISHED
import dev.rkashapov.testing.converter.TestSessionEntity2SimpleTestStatisticsModelConverter
import dev.rkashapov.testing.repository.QuestionAnswerRepository
import dev.rkashapov.testing.repository.TestSessionRepository
import dev.rkashapov.user.repository.UserRepository
import mu.KLogging
import org.springframework.stereotype.Service
import java.util.*

@Service
class TestStatisticsService(
    private val testSessionRepository: TestSessionRepository,
    private val userRepository: UserRepository,
    private val questionAnswerRepository: QuestionAnswerRepository,
    private val testSessionEntity2SimpleTestStatisticsModelConverter: TestSessionEntity2SimpleTestStatisticsModelConverter
) : KLogging() {

    fun getAllCompletedSessions(userId: UUID): List<SimpleTestStatisticsModel> {
        return withLoggingContext(USER_ID to userId) {
            val user = userRepository.getReferenceById(userId)

            val completedSessions =
                testSessionRepository.findAllByRespondentAndStatusOrderByUpdatedAtDesc(
                    respondent = user,
                    status = FINISHED
                )

            logger.debug { "completedSessions: $completedSessions" }

            completedSessions
                .map { session -> testSessionEntity2SimpleTestStatisticsModelConverter.convert(session) }
                .also { logger.debug { "statistic: $it" } }
        }
    }


    fun getStatisticsBySessionId(sessionId: UUID): ExtendedTestStatisticsModel {
        withLoggingContext(TEST_SESSION_ID to sessionId) {
            val session = testSessionRepository.findById(sessionId).orElseThrow()

            val answers = questionAnswerRepository.findAllBySessionOrderByCreatedAt(session)

            logger.debug { "found answers: $answers" }

            val difficultyChange = answers.mapIndexed { index, answer -> index + 1 to answer.question.difficulty }
            val answersPerSkill = answers.groupBy { it.question.skills.first().name }
            val ranksPerSkill = answersPerSkill.mapValues { skillAnswers -> skillAnswers.value.map { it.rank() } }
            val wrongAnswersPerSkill =
                ranksPerSkill.mapValues { skillRanks -> skillRanks.value.count { rank -> rank < 0.85 } }
            val correctAnswersPerSkill =
                ranksPerSkill.mapValues { skillRanks -> skillRanks.value.count { rank -> rank >= 0.85 } }

            return ExtendedTestStatisticsModel(
                sessionId = checkNotNull(session.sessionId),
                testId = checkNotNull(session.test.id),
                testName = session.test.name,
                startedAt = session.createdAt,
                finishedAt = session.updatedAt,
                difficultyChange = difficultyChange
                    .map { indexedDifficulty ->
                        NameDifficultyData(
                            name = indexedDifficulty.first.toString(),
                            value = indexedDifficulty.second
                        )
                    }
                    .plus(
                        NameDifficultyData(
                            name = (difficultyChange.size + 1).toString(),
                            value = answers.last().nextDifficulty()
                        )
                    ),
                correctAnswersCountPerSkill = correctAnswersPerSkill.map { entry ->
                    NameValueData(
                        name = entry.key,
                        value = entry.value
                    )
                },
                wrongAnswersCountPerSkill = wrongAnswersPerSkill.map { entry ->
                    NameValueData(
                        name = entry.key,
                        value = entry.value
                    )
                }
            ).also { logger.debug { "statistic: $it" } }
        }

    }


}
