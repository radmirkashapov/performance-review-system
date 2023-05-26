package dev.rkashapov.prs.testing.api.model

enum class QuestionDifficulty(val difficulty: Int) {
    TRAINEE(difficulty = 0),
    JUNIOR_MINUS(difficulty = 1),
    JUNIOR(difficulty = 2),
    JUNIOR_PLUS(difficulty = 3),
    MIDDLE_MINUS(difficulty = 4),
    MIDDLE(difficulty = 5),
    MIDDLE_PLUS(difficulty = 6),
    SENIOR_MINUS(difficulty = 7),
    SENIOR(difficulty = 8),
    SENIOR_PLUS(difficulty = 9),
    LEAD_MINUS(difficulty = 10),
    LEAD(difficulty = 11),
    LEAD_PLUS(difficulty = 12);

    @OptIn(ExperimentalStdlibApi::class)
    fun previousOrCurrentIfNotExists(): QuestionDifficulty {
        return QuestionDifficulty.entries.find { it.difficulty == this.difficulty - 1 } ?: this
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun nextOrCurrentIfNotExists(): QuestionDifficulty {
        return QuestionDifficulty.entries.find { it.difficulty == this.difficulty + 1 } ?: this
    }
}
