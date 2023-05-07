package dev.rkashapov.security.oauth.entity

import dev.rkashapov.user.entity.User
import jakarta.persistence.*
import org.hibernate.annotations.ColumnTransformer
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "oauth_token")
data class OAuthToken(
    @Id
    @GeneratedValue
    var id: UUID? = null,

    @ColumnTransformer(
        forColumn = "access_token",
        write = "pgp_sym_encrypt(?, current_setting('encrypt.access_token_key'))",
        read = "pgp_sym_decrypt(access_token, current_setting('encrypt.access_token_key'))"
    )
    @Column(name = "access_token")
    val accessToken: String,

    @ColumnTransformer(
        forColumn = "refresh_token",
        write = "pgp_sym_encrypt(?, current_setting('encrypt.refresh_token_key'))",
        read = "pgp_sym_decrypt(refresh_token, current_setting('encrypt.refresh_token_key'))"
    )
    @Column(name = "refresh_token")
    val refreshToken: String,

    // for yandex expiration time equals for both tokens
    @Column(name = "expires_in_seconds")
    val expiresInSeconds: Long,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    val owner: User
) {
    @Column(name = "created_at")
    val createdAt: Instant = Instant.now()

    // expires in <actual expiration datetime minus 1 minute>
    @Transient
    val expiresIn: LocalDateTime = LocalDateTime.from(createdAt).plusSeconds(expiresInSeconds).minusMinutes(1L)
}
