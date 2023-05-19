package dev.rkashapov.testing.api.controller

import dev.rkashapov.prs.testing.api.model.TestModel
import dev.rkashapov.testing.api.annotation.TestRestController
import dev.rkashapov.testing.service.TestService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.*

@TestRestController
class TestController(
    private val testService: TestService
) {

    @GetMapping
    fun getTests(): ResponseEntity<List<TestModel>> { // FIXME rewrite to pagination soon
        return ResponseEntity.ok(testService.getAll())
    }

    @GetMapping("{testId}")
    fun getById(@PathVariable testId: UUID): ResponseEntity<TestModel> {
        return ResponseEntity.ok(testService.getById(testId))
    }

}
