package dev.rkashapov.testing.repository

import dev.rkashapov.testing.entity.CheckListQuestionEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface CheckListQuestionRepository : JpaRepository<CheckListQuestionEntity, UUID>
