package dev.rkashapov.testing.entity

import dev.rkashapov.prs.testing.api.model.QuestionAnswerType
import dev.rkashapov.prs.testing.api.model.QuestionDifficulty
import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Type
import java.time.Instant
import java.util.*

@Entity
@Table(name = "question_answer")
data class QuestionAnswerEntity(
    @Id
    @GeneratedValue
    var id: UUID? = null,

    @field:NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    val question: QuestionEntity,

    @Type(value = JsonType::class)
    @Column(name = "answer", columnDefinition = "JSON")
    val answer: Map<QuestionAnswerType, List<String>> = emptyMap(),

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    val createdAt: Instant = Instant.now(),

    @field:NotNull
    @JoinColumn(name = "session_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val session: TestSessionEntity
) {
    fun nrmCoefficient(): Double = question.difficulty.difficulty.toDouble() / QuestionDifficulty.LEAD_PLUS.difficulty

    fun rank(): Double {
        return if (answer.getOrDefault(QuestionAnswerType.ANSWER_BY_PROBLEM, emptyList()).isNotEmpty()) {
            1.0
        } else {
            val userAnswer = answer.getOrDefault(QuestionAnswerType.ANSWER_BY_QUESTION, emptyList())
            question.correctAnswers.size.takeIf { it != 0 }?.let {
                (userAnswer.intersect(question.correctAnswers.toSet()).size.toDouble() / question.correctAnswers.size)
            } ?: run {
                if (question.correctAnswers.isEmpty() && userAnswer.isEmpty()) {
                    1.0
                } else 0.0
            }
        }
    }

    fun nextDifficulty(): QuestionDifficulty {
        return when (val answerRank = rank()) {
            1.0 -> question.difficulty.nextOrCurrentIfNotExists()
            0.0 -> question.difficulty.previousOrCurrentIfNotExists()
            else -> {
                if (answerRank >= 0.85) {
                    question.difficulty.nextOrCurrentIfNotExists()
                } else {
                    question.difficulty.previousOrCurrentIfNotExists()
                }
            }
        }
    }
}
