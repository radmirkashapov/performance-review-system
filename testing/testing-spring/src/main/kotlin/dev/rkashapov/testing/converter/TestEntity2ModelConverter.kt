package dev.rkashapov.testing.converter

import dev.rkashapov.prs.testing.api.model.TestModel
import dev.rkashapov.testing.entity.TestEntity
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class TestEntity2ModelConverter : Converter<TestEntity, TestModel> {

    override fun convert(source: TestEntity): TestModel {
        return TestModel(
            id = checkNotNull(source.id),
            name = source.name,
            description = source.description
        )
    }

}
