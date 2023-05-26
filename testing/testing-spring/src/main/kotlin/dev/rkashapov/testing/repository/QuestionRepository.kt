package dev.rkashapov.testing.repository

import dev.rkashapov.prs.testing.api.model.QuestionDifficulty
import dev.rkashapov.testing.entity.QuestionEntity
import dev.rkashapov.testing.entity.SkillEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface QuestionRepository : JpaRepository<QuestionEntity, UUID> {
    fun findFirstByIdNotInAndDifficultyAndSkillsContains(answeredQuestions: List<UUID>, difficulty: QuestionDifficulty, skill: SkillEntity): QuestionEntity?
}
