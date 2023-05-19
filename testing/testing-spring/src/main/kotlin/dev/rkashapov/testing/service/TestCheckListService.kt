package dev.rkashapov.testing.service

import dev.rkashapov.base.logging.MdcKey.TEST_CHECKLIST_ID
import dev.rkashapov.base.logging.MdcKey.TEST_ID
import dev.rkashapov.base.logging.withLoggingContext
import dev.rkashapov.prs.testing.api.model.UserRelatedTestCheckListModel
import dev.rkashapov.testing.converter.TestCheckListEntity2UserRelatedModelConverter
import dev.rkashapov.testing.repository.TestCheckListRepository
import dev.rkashapov.testing.repository.TestRepository
import mu.KLogging
import org.springframework.stereotype.Service
import java.util.*

@Service
class TestCheckListService(
    private val testRepository: TestRepository,
    private val testCheckListEntity2ModelConverter: TestCheckListEntity2UserRelatedModelConverter,
    private val testCheckListRepository: TestCheckListRepository
) : KLogging() {

    fun getUserTestCheckList(testId: UUID): UserRelatedTestCheckListModel = withLoggingContext(TEST_ID to testId) {

        val test = testRepository.getReferenceById(testId)

        val checkList = requireNotNull(testCheckListRepository.findByTest(test)) {
            "Checklist for test=$testId not found"
        }

        withLoggingContext(TEST_CHECKLIST_ID to checkList.id) {
            logger.debug { "Found checklist: $checkList" }

            testCheckListEntity2ModelConverter.convert(checkList)
        }
    }

}
