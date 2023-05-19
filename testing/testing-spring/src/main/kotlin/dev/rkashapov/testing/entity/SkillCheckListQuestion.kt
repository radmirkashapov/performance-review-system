package dev.rkashapov.testing.entity

import jakarta.persistence.*
import java.io.Serializable
import java.util.*

@Entity
@Table(name = "skill_check_list_question")
data class SkillCheckListQuestion(
    @EmbeddedId
    val id: SkillCheckListQuestionKey,

    @ManyToOne
    @MapsId("name")
    @JoinColumn(name = "skill_name")
    val skill: SkillEntity,

    @ManyToOne
    @MapsId("id")
    @JoinColumn(name = "question_id")
    val question: CheckListQuestionEntity,
) : Serializable {
    constructor(skill: SkillEntity, question: CheckListQuestionEntity) : this(
        SkillCheckListQuestionKey(skill.name, checkNotNull(question.id)),
        skill,
        question
    )
}

@Embeddable
data class SkillCheckListQuestionKey(
    @Column(name = "skill_name")
    val skillName: String,

    @Column(name = "question_id")
    val questionId: UUID
) : Serializable
