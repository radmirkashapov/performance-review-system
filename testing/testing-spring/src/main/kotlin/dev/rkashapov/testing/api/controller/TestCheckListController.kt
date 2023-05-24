package dev.rkashapov.testing.api.controller

import dev.rkashapov.prs.testing.api.model.TestCheckListDoAnswerRequest
import dev.rkashapov.prs.testing.api.model.TestCheckListDoAnswerResponse
import dev.rkashapov.prs.testing.api.model.UserRelatedTestCheckList
import dev.rkashapov.security.core.service.AuthorizationService
import dev.rkashapov.testing.api.annotation.TestCheckListRestController
import dev.rkashapov.testing.service.TestCheckListAnswerService
import dev.rkashapov.testing.service.TestCheckListService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.*

@TestCheckListRestController
@Validated
class TestCheckListController(
    private val testCheckListService: TestCheckListService,
    private val testCheckListAnswerService: TestCheckListAnswerService,
    private val authorizationService: AuthorizationService
) {

    @GetMapping
    fun getUserTestCheckList(@PathVariable testId: UUID): ResponseEntity<UserRelatedTestCheckList> {
        val user = authorizationService.currentUserOrDie()
        return ResponseEntity.ok(testCheckListService.getUserTestCheckList(testId = testId, userId = user.id))
    }

    @PostMapping("do-answer")
    @Transactional
    fun doAnswer(
        @PathVariable testId: UUID,
        @RequestBody @Valid body: TestCheckListDoAnswerRequest
    ): ResponseEntity<TestCheckListDoAnswerResponse> {
        val user = authorizationService.currentUserOrDie()

        return testCheckListAnswerService
            .doAnswer(
                respondentId = user.id,
                testId = testId,
                sessionId = body.sessionId,
                questionId = body.questionId,
                answer = body.answer,
                skillName = body.skillName
            ).let { ResponseEntity.ok(it) }
    }

}
