package dev.rkashapov.testing.converter

import dev.rkashapov.prs.testing.api.model.NextQuestion
import dev.rkashapov.prs.testing.api.model.PerformanceReviewDoAnswerResponse
import dev.rkashapov.prs.testing.api.model.QuestionType
import dev.rkashapov.testing.entity.QuestionEntity
import org.springframework.stereotype.Component
import java.util.function.BiFunction

@Component
class QuestionEntity2NextQuestionModelConverter : BiFunction<QuestionEntity, String, NextQuestion> {

    override fun apply(source: QuestionEntity, nextSkill: String): NextQuestion {
        return PerformanceReviewDoAnswerResponse(
            nextSkill = nextSkill,
            nextQuestion = source.question,
            nextQuestionId = checkNotNull(source.id),
            nextQuestionAnswerOptions = source.answerOptions,
            nextQuestionType = if(source.correctAnswers.size > 1) {
                QuestionType.MULTIPLE_CHOICE
            } else {
                QuestionType.SINGLE_ANSWER
            }
        )
    }

    fun convert(source: QuestionEntity, nextSkill: String): NextQuestion = apply(source, nextSkill)

}
