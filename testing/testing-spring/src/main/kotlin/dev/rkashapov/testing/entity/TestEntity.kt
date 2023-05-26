package dev.rkashapov.testing.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "test")
data class TestEntity(
    @Id
    @GeneratedValue
    var id: UUID? = null,

    @field:NotNull
    @Column(name = "name", nullable = false)
    val name: String,

    @field:NotNull
    @Column(name = "description", nullable = false)
    val description: String,

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
) {

    @OneToOne(cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    var checkList: TestCheckListEntity? = null

    @OneToOne(cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    var matrix: TopicMatrixConnectivityMock? = null
}
