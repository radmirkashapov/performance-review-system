package dev.rkashapov.testing.function

import dev.rkashapov.base.logging.MdcKey.*
import dev.rkashapov.base.logging.withLoggingContext
import dev.rkashapov.prs.testing.api.model.NextQuestion
import dev.rkashapov.prs.testing.api.model.QuestionDifficulty
import dev.rkashapov.prs.testing.api.model.TestCheckListQuestionOption
import dev.rkashapov.testing.converter.QuestionEntity2NextQuestionModelConverter
import dev.rkashapov.testing.entity.SkillEntity
import dev.rkashapov.testing.entity.TestSessionEntity
import dev.rkashapov.testing.repository.QuestionRepository
import dev.rkashapov.testing.service.ChatCompletionQuestionService
import mu.KLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class NextQuestionSupplier(
    private val questionRepository: QuestionRepository,
    private val chatCompletionQuestionService: ChatCompletionQuestionService,
    private val questionEntity2NextQuestionModelConverter: QuestionEntity2NextQuestionModelConverter,
) : KLogging() {

    @Transactional
    fun getNextQuestion(
        session: TestSessionEntity,
        skill: SkillEntity,
        alreadyAnsweredQuestionsIds: List<UUID>,
        checkListAnswersPerSkill: Map<String, List<TestCheckListQuestionOption>>,
        requestedDifficulty: QuestionDifficulty,
        additionalDifficulties: Set<QuestionDifficulty> = setOf()
    ): NextQuestion {
        return withLoggingContext(
            TEST_ID to session.sessionId,
            TEST_SESSION_ID to session.sessionId,
            USER_ID to session.respondent.id
        ) {
            logger.debug { "Asking question by skill: ${skill.name}" }

            val nextQuestion = questionRepository
                .findFirstByIdNotInAndDifficultyAndSkillsContains(
                    alreadyAnsweredQuestionsIds,
                    QuestionDifficulty.MIDDLE,
                    skill
                )

            if (nextQuestion != null) {
                questionEntity2NextQuestionModelConverter
                    .convert(
                        source = nextQuestion,
                        nextSkill = skill.name
                    )
                    .also {
                        logger.debug { "next question: $it" }
                    }
            } else {
                val generatedQuestions = chatCompletionQuestionService.retrieve(
                    userId = checkNotNull(session.respondent.id),
                    testName = session.test.name,
                    skill = skill,
                    skillCheckListAnswer = checkListAnswersPerSkill.getValue(skill.name).toSet(),
                    requestedDifficulties = setOf(requestedDifficulty).plus(additionalDifficulties)
                )

                questionEntity2NextQuestionModelConverter.convert(
                    source = generatedQuestions.find { it.difficulty == requestedDifficulty }
                        ?: generatedQuestions.first(),
                    nextSkill = skill.name
                ).also {
                    logger.debug { "next question: $it" }
                }
            }
        }
    }
}
