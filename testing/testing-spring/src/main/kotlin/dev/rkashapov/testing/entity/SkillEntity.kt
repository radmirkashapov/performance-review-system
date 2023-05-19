package dev.rkashapov.testing.entity

import jakarta.persistence.*

@Entity
@Table(name = "skill")
class SkillEntity(
    @Id
    val name: String,

    @OneToMany(orphanRemoval = true, mappedBy = "skill")
    val relatedQuestions: MutableSet<SkillQuestion> = mutableSetOf(),

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "skill")
    val relatedCheckListQuestions: MutableSet<SkillCheckListQuestion> = mutableSetOf()
)
