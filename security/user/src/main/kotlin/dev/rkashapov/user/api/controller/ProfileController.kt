package dev.rkashapov.user.api.controller

import dev.rkashapov.security.core.service.AuthorizationService
import dev.rkashapov.user.api.annotation.UserRestController
import dev.rkashapov.user.api.model.UserInfo
import dev.rkashapov.user.service.ProfileService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping

@UserRestController
class ProfileController(
    private val authorizationService: AuthorizationService,
    private val profileService: ProfileService
) {

    @GetMapping("me")
    fun getCurrentUser(): ResponseEntity<UserInfo> {
        return ResponseEntity.ok(profileService.me(authorizationService.currentUserOrDie()))
    }
}
