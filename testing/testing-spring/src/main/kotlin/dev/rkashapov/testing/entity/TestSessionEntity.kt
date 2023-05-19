package dev.rkashapov.testing.entity

import dev.rkashapov.prs.testing.api.model.TestSessionStatus
import dev.rkashapov.user.entity.UserEntity
import jakarta.persistence.*
import jakarta.persistence.EnumType.STRING
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "test_session")
data class TestSessionEntity(
    @Id
    @GeneratedValue
    @Column(name = "session_id")
    val sessionId: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    val test: TestEntity,

    @JoinColumn(name = "respondent_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    val respondent: UserEntity,

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    var updatedAt: Instant = Instant.now(),

    @Enumerated(STRING)
    var status: TestSessionStatus = TestSessionStatus.ACTIVE
)
