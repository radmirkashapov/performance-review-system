package dev.rkashapov.testing.repository

import dev.rkashapov.testing.entity.SkillEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SkillRepository : JpaRepository<SkillEntity, String> {
}
