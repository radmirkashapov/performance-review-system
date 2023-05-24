package dev.rkashapov.testing.service

import dev.rkashapov.base.logging.MdcKey.*
import dev.rkashapov.base.logging.withLoggingContext
import dev.rkashapov.prs.testing.api.model.TestSessionStatus.ACTIVE
import dev.rkashapov.prs.testing.api.model.TestSessionStatus.DELAYED
import dev.rkashapov.prs.testing.api.model.UserRelatedTestCheckList
import dev.rkashapov.prs.testing.api.model.UserRelatedTestCheckListSkipModel
import dev.rkashapov.testing.converter.TestCheckListEntity2UserRelatedModelConverter
import dev.rkashapov.testing.repository.CheckListQuestionAnswerRepository
import dev.rkashapov.testing.repository.TestCheckListRepository
import dev.rkashapov.testing.repository.TestRepository
import dev.rkashapov.testing.repository.TestSessionRepository
import dev.rkashapov.user.repository.UserRepository
import mu.KLogging
import org.springframework.stereotype.Service
import java.util.*

@Service
class TestCheckListService(
    private val testRepository: TestRepository,
    private val testCheckListEntity2ModelConverter: TestCheckListEntity2UserRelatedModelConverter,
    private val userRepository: UserRepository,
    private val testSessionRepository: TestSessionRepository,
    private val checkListAnswerRepository: CheckListQuestionAnswerRepository,
    private val testCheckListRepository: TestCheckListRepository
) : KLogging() {

    fun getUserTestCheckList(testId: UUID, userId: UUID): UserRelatedTestCheckList = withLoggingContext(TEST_ID to testId) {

        val test = testRepository.getReferenceById(testId)
        val user = userRepository.getReferenceById(userId)

        val checkList = requireNotNull(testCheckListRepository.findByTest(test)) {
            "Checklist for test=$testId not found"
        }

        val session  = testSessionRepository.findFirstByRespondentAndTestAndStatusIn(
            respondent = user,
            test = test,
            statuses = listOf(ACTIVE, DELAYED)
        )

        requireNotNull(session) { "Test session not found" }

        withLoggingContext(TEST_CHECKLIST_ID to checkList.id, TEST_SESSION_ID to session.sessionId) {
            logger.debug { "Found checklist: $checkList" }

            val checkListAnswers = checkListAnswerRepository.findAllBySession(session).map { it.skill.name }.toSet()
            val checkListSkills = checkList.relatedSkills.map { it.name }.toSet()

            if (checkListSkills.intersect(checkListSkills).size == checkListSkills.size) {
                return UserRelatedTestCheckListSkipModel()
            }

            testCheckListEntity2ModelConverter.convert(checkList)
        }
    }

}
