package dev.rkashapov.prs.testing.api.model

import java.util.*

interface UserRelatedTestCheckList

data class UserRelatedTestCheckListSkipModel(
    val skip: Boolean = true
) : UserRelatedTestCheckList

data class UserRelatedTestCheckListModel(
    val id: UUID,
    val testId: UUID,

    /**
     * key - skill
     * value - check-list questions by skill
     * */
    val questionsBySkills: Map<String, List<CheckListQuestion>>
) : UserRelatedTestCheckList {
    class CheckListQuestion(
        val id: UUID,
        val question: String,
        val options: Set<TestCheckListQuestionOption>
    )
}
