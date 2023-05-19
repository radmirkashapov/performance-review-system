package dev.rkashapov.prs.testing.api.model

import java.util.*

data class UserRelatedTestCheckListModel(
    val id: UUID,
    val testId: UUID,

    /**
     * key - skill
     * value - check-list questions by skill
     * */
    val questionsBySkills: Map<String, List<CheckListQuestion>>
) {
    class CheckListQuestion(
        val id: UUID,
        val question: String,
        val options: Set<TestCheckListQuestionOption>
    )
}
