package dev.rkashapov.testing.converter

import dev.rkashapov.prs.testing.api.model.TestSessionModel
import dev.rkashapov.testing.entity.TestSessionEntity
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class TestSessionEntity2ModelConverter : Converter<TestSessionEntity, TestSessionModel> {

    override fun convert(source: TestSessionEntity): TestSessionModel {
        return TestSessionModel(
            id = checkNotNull(source.sessionId),
            testId = checkNotNull(source.test.id),
            startedAt = source.createdAt,
            checkListId = source.test.checkList?.id
        )
    }

}
