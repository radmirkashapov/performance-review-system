package dev.rkashapov.security.oauth.entity

import dev.rkashapov.user.entity.UserEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.ColumnTransformer
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "oauth_token")
class OAuthTokenEntity(
    @Id
    @GeneratedValue
    var id: UUID? = null,

    @NotNull
    @Column(name = "device_id")
    val deviceId: UUID,

    @Column(name = "device_name")
    @ColumnTransformer(
        forColumn = "device_name",
        write = "pgp_sym_encrypt(?, current_setting('encrypt.personal_info_key'))",
        read = "pgp_sym_decrypt(email, current_setting('encrypt.personal_info_key'))"
    )
    val deviceName: String,
) {
    @ColumnTransformer(
        forColumn = "access_token",
        write = "pgp_sym_encrypt(?, current_setting('encrypt.access_token_key'))",
        read = "pgp_sym_decrypt(access_token, current_setting('encrypt.access_token_key'))"
    )
    @Column(name = "access_token")
    var accessToken: String? = null

    @ColumnTransformer(
        forColumn = "refresh_token",
        write = "pgp_sym_encrypt(?, current_setting('encrypt.refresh_token_key'))",
        read = "pgp_sym_decrypt(refresh_token, current_setting('encrypt.refresh_token_key'))"
    )
    @Column(name = "refresh_token")
    var refreshToken: String? = null

    @Column(name = "created_at")
    @CreationTimestamp
    val createdAt: Instant = Instant.now()

    @Column(name = "updated_at")
    @UpdateTimestamp
    var updatedAt: Instant? = null

    // for yandex expiration time equals for both tokens
    @Transient
    var expiresIn: LocalDateTime? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    var owner: UserEntity? = null

    fun isInitialized() = owner != null
}
