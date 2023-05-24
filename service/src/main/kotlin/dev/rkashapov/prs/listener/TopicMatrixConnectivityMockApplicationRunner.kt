package dev.rkashapov.prs.listener

import com.openai.client.api.client.MatrixOfTopicConnectivityClient
import dev.rkashapov.testing.entity.TopicMatrixConnectivityMock
import dev.rkashapov.testing.repository.TestCheckListRepository
import dev.rkashapov.testing.repository.TestRepository
import dev.rkashapov.testing.repository.TopicMatrixConnectivityMockRepository
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(2)
class TopicMatrixConnectivityMockApplicationRunner(
    private val testRepository: TestRepository,
    private val topicMatrixConnectivityMockRepository: TopicMatrixConnectivityMockRepository,
    private val matrixOfTopicConnectivityClient: MatrixOfTopicConnectivityClient,
    private val testCheckListRepository: TestCheckListRepository
) : ApplicationRunner, KLogging() {

    override fun run(args: ApplicationArguments) {
        logger.info { "Starting ${TopicMatrixConnectivityMockApplicationRunner::class.simpleName}..." }
        initTopicMatrices()

        logger.info { "Finished ${TopicMatrixConnectivityMockApplicationRunner::class.simpleName}" }

    }

    fun initTopicMatrices() {
        val matrices = testRepository
            .findAll()
            .mapNotNull { test ->

                logger.info { "Processing test: $test" }

                val matrix = topicMatrixConnectivityMockRepository.findByTest(test)

                if (matrix != null) {
                    logger.info { "Matrix already initialized" }
                    return@mapNotNull null
                }

                val skills = checkNotNull(testCheckListRepository.findByTest(test)).relatedSkills.map { it.name }.sorted()

                val computedMatrix = runBlocking {
                    matrixOfTopicConnectivityClient
                        .getMatrix(testName = test.name, sortedSkills = skills)
                }

                TopicMatrixConnectivityMock(
                    matrixId = checkNotNull(test.id),
                    test = test,
                    matrixRowValuesPerSkill = computedMatrix.mapIndexed { index, row -> skills[index] to row }.toMap()
                )
            }

        topicMatrixConnectivityMockRepository.saveAll(matrices)

        logger.debug { "Generated matrices: $matrices" }
    }
}
