package dev.rkashapov.testing.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "test_check_list")
data class TestCheckListEntity(
    @Id
    @Column(name = "test_id", nullable = false)
    var id: UUID? = null,

    @MapsId("id")
    @OneToOne(mappedBy = "checkList")
    @JoinColumn(name = "test_id")
    val test: TestEntity,

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "test_check_list_skill",
        joinColumns = [JoinColumn(name = "test_check_list_id", referencedColumnName = "test_id")],
        inverseJoinColumns = [JoinColumn(name = "skill_name", referencedColumnName = "name")]
    )
    val relatedSkills: MutableSet<SkillEntity> = mutableSetOf()
)
