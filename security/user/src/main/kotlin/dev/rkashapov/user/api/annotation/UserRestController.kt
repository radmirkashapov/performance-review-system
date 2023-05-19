package dev.rkashapov.user.api.annotation

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@Tag(name = "User operations")
@SecurityRequirement(name = AUTHORIZATION)
@PreAuthorize("isFullyAuthenticated()")
@RequestMapping("/api/v1/profile")
annotation class UserRestController
