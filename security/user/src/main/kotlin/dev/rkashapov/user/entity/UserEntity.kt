package dev.rkashapov.user.entity

import dev.rkashapov.base.model.UserRole
import dev.rkashapov.base.model.UserStatus
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.validator.constraints.URL
import java.time.Instant
import java.util.*

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue
    var id: UUID? = null,

    @Column(name = "email", unique = true, nullable = false)
    @NotBlank
    val email: String,

    @NotBlank
    @Column(name = "real_name", nullable = false)
    var realName: String,

    @URL
    @Column(name = "avatar_url", nullable = false)
    var avatarUrl: String
) {
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    val createdAt: Instant = Instant.now()

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()

    @Enumerated(EnumType.STRING)
    var role: UserRole = UserRole.USER

    @Enumerated(EnumType.STRING)
    val status: UserStatus = UserStatus.ACTIVE

}
