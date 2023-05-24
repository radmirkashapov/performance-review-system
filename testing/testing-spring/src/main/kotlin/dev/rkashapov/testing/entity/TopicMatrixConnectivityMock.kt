package dev.rkashapov.testing.entity

import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.*
import jakarta.validation.constraints.NotEmpty
import org.hibernate.annotations.Type
import java.util.*

@Entity
@Table(name = "matrix_mock")
data class TopicMatrixConnectivityMock(
    @Id
    @Column(name = "matrix_id", nullable = false)
    val matrixId: UUID? = null,

    @MapsId("matrixId")
    @OneToOne(mappedBy = "matrix")
    @JoinColumn(name = "matrix_id")
    val test: TestEntity,

    @field:NotEmpty
    @Type(value = JsonType::class)
    @Column(name = "matrix_row_values_per_skill", columnDefinition = "JSON")
    val matrixRowValuesPerSkill: Map<String, List<Double>> = mapOf()
)
