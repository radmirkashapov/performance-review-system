package dev.rkashapov.testing.service

import dev.rkashapov.base.logging.MdcKey.TEST_ID
import dev.rkashapov.base.logging.withLoggingContext
import dev.rkashapov.testing.repository.TopicMatrixConnectivityMockRepository
import mu.KLogging
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserTopicMatrixConnectivityMockService(
    private val topicMatrixConnectivityMockRepository: TopicMatrixConnectivityMockRepository,
) : KLogging() {

    fun getMatrixByUsersSkills(testId: UUID, selectedSkills: List<String>): Map<String, List<Double>> {
        withLoggingContext(TEST_ID to testId) {
            logger.info { "Calculating matrix of topics connectivity for skills: $selectedSkills" }
            val topicMatrixConnectivity = topicMatrixConnectivityMockRepository.findById(testId).orElseThrow()

            logger.debug { "Received matrix: $topicMatrixConnectivity" }

            val indexesToIgnore = topicMatrixConnectivity.matrixRowValuesPerSkill.keys.mapIndexedNotNull { index, skill ->
                index.takeIf { !selectedSkills.contains(skill) }
            }

            return topicMatrixConnectivity
                .matrixRowValuesPerSkill
                .filterKeys { key -> selectedSkills.contains(key) }
                .mapValues { entry -> entry.value.filterIndexed { index, _ -> !indexesToIgnore.contains(index) } }
        }
    }
}
