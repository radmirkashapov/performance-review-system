package dev.rkashapov.testing.converter

import dev.rkashapov.prs.testing.api.model.SimpleTestStatisticsModel
import dev.rkashapov.testing.entity.TestSessionEntity
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class TestSessionEntity2SimpleTestStatisticsModelConverter : Converter<TestSessionEntity, SimpleTestStatisticsModel> {
    override fun convert(source: TestSessionEntity): SimpleTestStatisticsModel {
        return SimpleTestStatisticsModel(
            sessionId = checkNotNull(source.sessionId),
            testId = checkNotNull(source.test.id),
            testName = source.test.name,
            startedAt = source.createdAt,
            finishedAt = source.updatedAt
        )
    }
}
