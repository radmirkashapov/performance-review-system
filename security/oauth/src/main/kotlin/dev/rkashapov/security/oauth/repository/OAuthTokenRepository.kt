package dev.rkashapov.security.oauth.repository

import dev.rkashapov.security.oauth.entity.OAuthToken
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OAuthTokenRepository : JpaRepository<OAuthToken, UUID> {
}
