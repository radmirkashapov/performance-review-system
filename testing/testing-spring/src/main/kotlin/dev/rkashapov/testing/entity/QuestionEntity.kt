package dev.rkashapov.testing.entity

import dev.rkashapov.prs.testing.api.model.QuestionDifficulty
import io.hypersistence.utils.hibernate.type.array.ListArrayType
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.Type
import java.util.*

@Entity
@Table(name = "question")
data class QuestionEntity(
    @Id
    @GeneratedValue
    var id: UUID? = null,

    @field:NotBlank
    @Column(name = "question")
    val question: String,

    @Type(value = ListArrayType::class)
    @Column(name = "answer_options", columnDefinition = "TEXT[]")
    val answerOptions: List<String> = listOf(),

    @Type(value = ListArrayType::class)
    @Column(name = "correct_answers", columnDefinition = "TEXT[]")
    val correctAnswers: List<String> = listOf(),

    @field:NotNull
    @Enumerated(EnumType.STRING)
    val difficulty: QuestionDifficulty
) {
    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "skill_question",
        joinColumns = [JoinColumn(name = "question_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "skill_name", referencedColumnName = "name")]
    )
    val skills: MutableSet<SkillEntity> = mutableSetOf()
}
