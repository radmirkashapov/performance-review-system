package dev.rkashapov.testing.converter

import dev.rkashapov.prs.testing.api.model.UserRelatedTestCheckListModel
import dev.rkashapov.testing.entity.TestCheckListEntity
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class TestCheckListEntity2UserRelatedModelConverter : Converter<TestCheckListEntity, UserRelatedTestCheckListModel> {

    /**
     * Converts checklist entity to model with questions grouped by skills
     * */
    override fun convert(source: TestCheckListEntity): UserRelatedTestCheckListModel {

        val id = checkNotNull(source.id)
        val testId = checkNotNull(source.test.id)

        val skillsByQuestions = source
            .relatedSkills
            .groupBy({ it.name }, { it.relatedCheckListQuestions })
            .mapValues { mapEntry ->
                mapEntry
                    .value
                    .flatten()
                    .map { skillCheckList ->
                        UserRelatedTestCheckListModel.CheckListQuestion(
                            id = checkNotNull(skillCheckList.question.id),
                            question = skillCheckList.question.question,
                            options = skillCheckList.question.answerOptions.sortedBy { it.rank }.toSet()
                        )
                    }
            }

        return UserRelatedTestCheckListModel(
            id = id,
            testId = testId,
            questionsBySkills = skillsByQuestions
        )
    }

}
