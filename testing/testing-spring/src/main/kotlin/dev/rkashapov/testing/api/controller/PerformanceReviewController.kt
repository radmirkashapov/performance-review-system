package dev.rkashapov.testing.api.controller

import dev.rkashapov.prs.testing.api.model.NextQuestion
import dev.rkashapov.prs.testing.api.model.PerformanceReviewDoAnswerRequest
import dev.rkashapov.security.core.service.AuthorizationService
import dev.rkashapov.testing.api.annotation.PerformanceReviewRestController
import dev.rkashapov.testing.service.PerformanceReviewService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.*

@PerformanceReviewRestController
@Validated
class PerformanceReviewController(
    private val performanceReviewService: PerformanceReviewService,
    private val authorizationService: AuthorizationService
) {

    @PostMapping("/{testId}/do-answer")
    fun doAnswer(
        @PathVariable testId: UUID,
        @RequestBody(required = false) @Valid body: PerformanceReviewDoAnswerRequest? = null // To receive first question
    ): ResponseEntity<NextQuestion> {
        val user = authorizationService.currentUserOrDie()

        return ResponseEntity.ok(performanceReviewService.doAnswer(testId = testId, userId = user.id, answer = body))
    }


}
