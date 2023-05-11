package dev.rkashapov.security.oauth.repository

import dev.rkashapov.security.oauth.entity.OAuthTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface OAuthTokenRepository : JpaRepository<OAuthTokenEntity, UUID> {
    fun findFirstByDeviceId(deviceId: String): OAuthTokenEntity?
}
