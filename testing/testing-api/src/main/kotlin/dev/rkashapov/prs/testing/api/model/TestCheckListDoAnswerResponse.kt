package dev.rkashapov.prs.testing.api.model

data class TestCheckListDoAnswerResponse(

    /**
     * If false, current skill questions in checklist should be skipped
     * */

    val skipSkill: Boolean = false,
)
