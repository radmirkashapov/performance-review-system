package dev.rkashapov.testing.function

import dev.rkashapov.base.logging.MdcKey.*
import dev.rkashapov.base.logging.withLoggingContext
import dev.rkashapov.testing.entity.PRSStateTestHZModel
import dev.rkashapov.testing.entity.QuestionAnswerEntity
import mu.KLogging
import org.jetbrains.kotlinx.multik.api.math.argMin
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.mapIndexed
import org.jetbrains.kotlinx.multik.ndarray.operations.sum
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import org.springframework.stereotype.Service
import java.util.function.BiFunction

// topic == skill
@Service
class NextTopicSupplier(
    private val averageRanksVectorSupplier: AverageRanksVectorSupplier
) : BiFunction<PRSStateTestHZModel, List<QuestionAnswerEntity>, String>, KLogging() {

    fun nextTopic(state: PRSStateTestHZModel, answers: List<QuestionAnswerEntity>): String = apply(state = state, answers = answers)

    override fun apply(state: PRSStateTestHZModel, answers: List<QuestionAnswerEntity>): String {
        return withLoggingContext(TEST_ID to state.testId, TEST_SESSION_ID to state.testId, USER_ID to state.userId) {

            logger.info { "Calculating next topic..." }

            val selectedSkills = state.topicMatrixConnectivity.keys
            val averageRanks = averageRanksVectorSupplier
                .averageRanksVectorWithEmptyColumns(selectedSkills = selectedSkills, answers = answers)

            val matrix = state
                .topicMatrixConnectivity
                .values
                .toList()
                .let { mk.ndarray(it) }

            val vectorOfTopicEvaluation = (matrix * mk.ndarray(averageRanks))[0]

            val normalizedVectorOfTopicEvaluation = vectorOfTopicEvaluation.mapIndexed { index, value ->
                value / matrix[index, selectedSkills.indices].sum()
            }

            normalizedVectorOfTopicEvaluation
                .argMin()
                .let { indexOfMax ->
                    val nextTopic = selectedSkills.toList()[indexOfMax]

                    logger.debug { "Next topic is: $nextTopic" }

                    nextTopic
                }
        }
    }
}
