package dev.rkashapov.testing.entity

import dev.rkashapov.prs.testing.api.model.QuestionDifficulty
import io.hypersistence.utils.hibernate.type.array.StringArrayType
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
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

    @Type(value = StringArrayType::class)
    @Column(name = "answer_options", columnDefinition = "TEXT[]")
    val answerOptions: MutableSet<String> = mutableSetOf(),

    @field:NotEmpty
    @Type(value = StringArrayType::class)
    @Column(name = "correct_answers", columnDefinition = "TEXT[]")
    val correctAnswers: MutableSet<String> = mutableSetOf(),

    @field:NotNull
    @Enumerated(EnumType.STRING)
    val difficulty: QuestionDifficulty
)
