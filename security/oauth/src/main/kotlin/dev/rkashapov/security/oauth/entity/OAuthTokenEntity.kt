package dev.rkashapov.security.oauth.entity

import dev.rkashapov.user.entity.UserEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.ColumnTransformer
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "oauth_token")
class OAuthTokenEntity(
    @Id
    @GeneratedValue
    var id: UUID? = null,

    @NotNull
    @Column(name = "device_id", nullable = false)
    val deviceId: String,

    @Column(name = "device_name", columnDefinition = "bytea", nullable = false)
    @ColumnTransformer(
        forColumn = "device_name",
        write = "pgp_sym_encrypt(?, current_setting('encrypt.personalInfo_key'))",
        read = "pgp_sym_decrypt(device_name, current_setting('encrypt.personalInfo_key'))"
    )
    val deviceName: String,

    @ColumnTransformer(
        forColumn = "access_token",
        write = "pgp_sym_encrypt(?, current_setting('encrypt.accessToken_key'))",
        read = "pgp_sym_decrypt(access_token, current_setting('encrypt.accessToken_key'))"
    )
    @Column(name = "access_token", columnDefinition = "bytea", nullable = false)
    var accessToken: String,

    @ColumnTransformer(
        forColumn = "refresh_token",
        write = "pgp_sym_encrypt(?, current_setting('encrypt.refreshToken_key'))",
        read = "pgp_sym_decrypt(refresh_token, current_setting('encrypt.refreshToken_key'))"
    )
    @Column(name = "refresh_token", columnDefinition = "bytea", nullable = false)
    var refreshToken: String,

    // for yandex expiration time equals for both tokens
    @Column(name = "expires_in", nullable = false)
    var expiresIn: Instant
) {

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    val createdAt: Instant = Instant.now()

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    var updatedAt: Instant? = null

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "owner_id", nullable = false)
    var owner: UserEntity? = null

    fun isInitialized() = owner != null
}
