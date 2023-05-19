package dev.rkashapov.testing.entity

import dev.rkashapov.prs.testing.api.model.TestCheckListQuestionOption
import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.*
import jakarta.validation.constraints.NotEmpty
import org.hibernate.annotations.Type
import java.util.*

@Entity
@Table(name = "check_list_question")
data class CheckListQuestionEntity(
    @Id
    @GeneratedValue
    var id: UUID? = null,

    val question: String,

    @field:NotEmpty
    @Type(value = JsonType::class)
    @Column(name = "answer_options", columnDefinition = "JSON")
    val answerOptions: Set<TestCheckListQuestionOption> = emptySet()
)
