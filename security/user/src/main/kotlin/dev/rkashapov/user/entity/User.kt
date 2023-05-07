package dev.rkashapov.user.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.ColumnTransformer
import org.hibernate.validator.constraints.URL
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "oauth_token")
data class User(
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

    @URL
    val avatarUrl: String? = null
) {
    val createdAt: Instant = Instant.now()
}
