package dev.rkashapov.testing.function

import dev.rkashapov.base.logging.MdcKey.*
import dev.rkashapov.base.logging.withLoggingContext
import dev.rkashapov.testing.entity.QuestionAnswerEntity
import mu.KLogging
import org.springframework.stereotype.Service
import java.util.function.BiFunction

@Service
class AverageRanksVectorSupplier : BiFunction<Set<String>, List<QuestionAnswerEntity>, List<Double>>, KLogging() {
    override fun apply(selectedSkills: Set<String>, answers: List<QuestionAnswerEntity>): List<Double> {
        val firstAnswer = answers.first()
        val sessionId = firstAnswer.session.sessionId
        val respondentId = firstAnswer.session.respondent.id

        return withLoggingContext(TEST_ID to sessionId, TEST_SESSION_ID to sessionId, USER_ID to respondentId) {
            logger.info { "Calculating average ranks..." }

            if (answers.isEmpty()) {
                return selectedSkills.map { 0.0 }
            }

            val answersByTopic =
                answers.groupBy { it.question.skills.map { it.name }.intersect(selectedSkills).first() }

            selectedSkills.map { skill ->
                val skillAnswers = answersByTopic.getOrDefault(skill, emptyList())

                if (skillAnswers.isEmpty()) {
                    return@map 0.0
                }

                skillAnswers.sumOf { answer -> answer.rank() * answer.nrmCoefficient() } / skillAnswers.sumOf { answer -> answer.nrmCoefficient() }
            }.also {
                logger.debug { "Calculated average ranks: $it" }
            }
        }
    }

    fun averageRanksVector(selectedSkills: Set<String>, answers: List<QuestionAnswerEntity>): List<Double> =
        apply(selectedSkills = selectedSkills, answers = answers)

    fun averageRanksVectorWithEmptyColumns(selectedSkills: Set<String>, answers: List<QuestionAnswerEntity>): List<List<Double>> {
        val ranks = apply(selectedSkills = selectedSkills, answers = answers)

        val zeroColumns = generateEmptyColumns(columnsCount = selectedSkills.size - 1, size = selectedSkills.size)

        return listOf(ranks).plus(zeroColumns)
    }

    private fun generateEmptyColumns(columnsCount: Int, size: Int): List<List<Double>> {
        return (0 until columnsCount).map { arrayOfNulls<Double>(size).map { 0.0 } }
    }
}
