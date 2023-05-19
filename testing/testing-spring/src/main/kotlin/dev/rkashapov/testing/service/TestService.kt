package dev.rkashapov.testing.service

import dev.rkashapov.prs.testing.api.model.TestModel
import dev.rkashapov.testing.converter.TestEntity2ModelConverter
import dev.rkashapov.testing.repository.TestRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class TestService(
    private val testRepository: TestRepository,
    private val testEntity2ModelConverter: TestEntity2ModelConverter,
) {

    fun getAll(): List<TestModel> =
        testRepository.findAll().map { testEntity2ModelConverter.convert(it) } // we can use projections here

    @Throws(NoSuchElementException::class)
    fun getById(testId: UUID): TestModel = testRepository // we can use projections here
        .findById(testId)
        .orElseThrow()
        .let { testEntity2ModelConverter.convert(it) }

}
