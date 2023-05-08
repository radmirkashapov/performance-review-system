package dev.rkashapov.user.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import org.hibernate.annotations.ColumnTransformer
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.validator.constraints.URL
import java.time.Instant
import java.util.*

@Entity
@Table(name = "oauth_token")
data class UserEntity(
    @Id
    @GeneratedValue
    var id: UUID? = null,

    @ColumnTransformer(
        forColumn = "email",
        write = "pgp_sym_encrypt(?, current_setting('encrypt.personal_info_key'))",
        read = "pgp_sym_decrypt(email, current_setting('encrypt.personal_info_key'))"
    )
    @NotBlank
    val email: String,


    @ColumnTransformer(
        forColumn = "real_name",
        write = "pgp_sym_encrypt(?, current_setting('encrypt.personal_info_key'))",
        read = "pgp_sym_decrypt(email, current_setting('encrypt.personal_info_key'))"
    )
    @NotBlank
    @Column(name = "real_name")
    val realName: String,

    @URL
    @ColumnTransformer(
        forColumn = "avatar_url",
        write = "pgp_sym_encrypt(?, current_setting('encrypt.personal_info_key'))",
        read = "pgp_sym_decrypt(email, current_setting('encrypt.personal_info_key'))"
    )
    @Column(name = "avatar_url")
    var avatarUrl: String? = null
) {
    @Column(name = "created_at")
    @CreationTimestamp
    val createdAt: Instant = Instant.now()

    @Column(name = "updated_at")
    @UpdateTimestamp
    var updatedAt: Instant? = null
}
