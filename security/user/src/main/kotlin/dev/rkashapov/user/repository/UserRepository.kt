package dev.rkashapov.user.repository

import dev.rkashapov.user.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<UserEntity, UUID> {
    fun findFirstByEmail(email: String): UserEntity?
}
