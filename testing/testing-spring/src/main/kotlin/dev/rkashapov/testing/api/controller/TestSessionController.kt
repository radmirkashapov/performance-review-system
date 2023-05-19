package dev.rkashapov.testing.api.controller

import dev.rkashapov.prs.testing.api.model.TestSessionModel
import dev.rkashapov.security.core.service.AuthorizationService
import dev.rkashapov.testing.api.annotation.TestSessionRestController
import dev.rkashapov.testing.service.TestSessionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import java.util.*

@TestSessionRestController
class TestSessionController(
    private val authorizationService: AuthorizationService,
    private val testSessionService: TestSessionService
) {

    @PostMapping("/{testId}/sessions/start")
    fun startTestSession(@PathVariable testId: UUID): ResponseEntity<TestSessionModel> {
        val userId = authorizationService.currentUserOrDie().id
        return ResponseEntity.ok(testSessionService.startSession(testId = testId, userId = userId))
    }

    @PutMapping("/sessions/{sessionId}/finish")
    fun finishTestSession(@PathVariable sessionId: UUID): ResponseEntity<*> {
        return ResponseEntity.ok(testSessionService.finishSession(sessionId = sessionId))
    }

}
