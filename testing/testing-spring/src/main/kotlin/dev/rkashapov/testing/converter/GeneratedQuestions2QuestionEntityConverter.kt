package dev.rkashapov.testing.converter

import com.openai.client.api.model.GeneratedQuestionsModel
import dev.rkashapov.testing.entity.QuestionEntity
import dev.rkashapov.testing.entity.SkillEntity
import org.springframework.stereotype.Component

@Component
class GeneratedQuestions2QuestionEntityConverter {
    fun convert(source: GeneratedQuestionsModel, skill: SkillEntity): List<QuestionEntity> {
        return source
            .questions
            .map { question ->
                QuestionEntity(
                    question = question.question,
                    answerOptions = question.answerOptions,
                    correctAnswers = question.correctAnswers,
                    difficulty = question.difficulty,
                ).apply {
                    this.skills.add(skill)
                }
            }
    }
}
