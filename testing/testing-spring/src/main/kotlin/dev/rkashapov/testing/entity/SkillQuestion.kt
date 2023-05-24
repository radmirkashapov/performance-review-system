package dev.rkashapov.testing.entity

import jakarta.persistence.*
import java.io.Serializable
import java.util.*

// FIXME to ManyToMany
@Entity
@Table(name = "skill_question")
data class SkillQuestion(
    @EmbeddedId
    val id: SkillQuestionKey,

    @ManyToOne
    @MapsId("name")
    @JoinColumn(name = "skill_name")
    val skill: SkillEntity,

    @ManyToOne
    @MapsId("id")
    @JoinColumn(name = "question_id")
    val question: QuestionEntity,
) {
    constructor(skill: SkillEntity, question: QuestionEntity) : this(
        SkillQuestionKey(skill.name, checkNotNull(question.id)),
        skill,
        question
    )
}

@Embeddable
data class SkillQuestionKey(
    @Column(name = "skill_name")
    val skillName: String,

    @Column(name = "question_id")
    val questionId: UUID
) : Serializable
