package dev.rkashapov.testing.api.annotation

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpHeaders
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@PreAuthorize("isFullyAuthenticated()")
@Tag(name = "Test statistics operations")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
@RequestMapping("/api/v1/test-statistics")
annotation class TestStatisticsRestController
