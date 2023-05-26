package dev.rkashapov.testing.api.controller

import dev.rkashapov.prs.testing.api.model.ExtendedTestStatisticsModel
import dev.rkashapov.prs.testing.api.model.SimpleTestStatisticsModel
import dev.rkashapov.security.core.service.AuthorizationService
import dev.rkashapov.testing.api.annotation.TestStatisticsRestController
import dev.rkashapov.testing.service.TestStatisticsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.*

@TestStatisticsRestController
class TestStatisticsController(
    private val testStatisticsService: TestStatisticsService,
    private val authorizationService: AuthorizationService
) {

    @GetMapping("{sessionId}")
    fun getStatisticsBySessionId(@PathVariable sessionId: UUID): ResponseEntity<ExtendedTestStatisticsModel> {
        return ResponseEntity.ok(testStatisticsService.getStatisticsBySessionId(sessionId = sessionId))
    }

    @GetMapping("/finished-sessions")
    fun getAllFinishedSessions(): ResponseEntity<List<SimpleTestStatisticsModel>> {
        val user = authorizationService.currentUserOrDie()

        return ResponseEntity.ok(testStatisticsService.getAllCompletedSessions(userId = user.id))
    }

}
