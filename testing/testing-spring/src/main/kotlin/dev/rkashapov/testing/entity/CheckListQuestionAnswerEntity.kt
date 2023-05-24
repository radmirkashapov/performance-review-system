package dev.rkashapov.testing.entity

import dev.rkashapov.prs.testing.api.model.TestCheckListQuestionOption
import dev.rkashapov.user.entity.UserEntity
import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Type
import java.time.Instant
import java.util.*

@Entity
@Table(name = "check_list_question_answer")
data class CheckListQuestionAnswerEntity(
    @Id
    @GeneratedValue
    var id: UUID? = null,

    @field:NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    val question: CheckListQuestionEntity,

    @field:NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_name")
    val skill: SkillEntity,

    @Type(value = JsonType::class)
    @Column(name = "answer", columnDefinition = "JSON")
    var answer: Set<TestCheckListQuestionOption>,

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    val createdAt: Instant = Instant.now(),

    @field:NotNull
    @JoinColumn(name = "respondent_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val respondent: UserEntity,

    @field:NotNull
    @JoinColumn(name = "session_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val session: TestSessionEntity
)
