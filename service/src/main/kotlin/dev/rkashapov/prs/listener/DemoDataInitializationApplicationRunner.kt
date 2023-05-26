package dev.rkashapov.prs.listener

import dev.rkashapov.base.logging.MdcKey
import dev.rkashapov.base.logging.withLoggingContext
import dev.rkashapov.prs.testing.api.model.TestCheckListQuestionOption
import dev.rkashapov.prs.testing.api.model.TestCheckListQuestionOptionRank
import dev.rkashapov.prs.testing.api.model.UserRelatedTestCheckListModel
import dev.rkashapov.testing.entity.CheckListQuestionEntity
import dev.rkashapov.testing.entity.SkillCheckListQuestion
import dev.rkashapov.testing.entity.SkillEntity
import dev.rkashapov.testing.repository.CheckListQuestionRepository
import dev.rkashapov.testing.repository.TestCheckListRepository
import dev.rkashapov.testing.repository.TestRepository
import mu.KLogging
import org.apache.commons.csv.CSVFormat
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.io.InputStream
import java.util.*

@Order(1)
@Component(value = "demoDataInitializationApplicationRunner")
class DemoDataInitializationApplicationRunner(
    private val checkListRepository: TestCheckListRepository,
    private val checkListQuestionRepository: CheckListQuestionRepository,
    private val testRepository: TestRepository
) : ApplicationRunner, KLogging() {

    companion object {
        private const val CSV = ".csv"
        private const val HEADER_SKILL = "SKILL"
        private const val HEADER_QUESTION_ID = "QUESTION_ID"
        private const val QUESTION_CONTENT_PREFIX =
            """Оцените свой уровень владения следующей компетенцией "%skill%", выбрав соответствующий пункт."""
    }

    @Value("\${demo-data-path:classpath*:/checklists/*}")
    private lateinit var resources: Array<Resource>


    override fun run(args: ApplicationArguments) {
        logger.info { "Starting ${DemoDataInitializationApplicationRunner::class.simpleName}..." }
        addAllDemoData()

        logger.info { "Finished ${DemoDataInitializationApplicationRunner::class.simpleName}" }
    }

    @Transactional
    fun addAllDemoData() {
        logger.info { "Processing demo resources..." }

        resources.forEach { resource ->
            val testId = requireNotNull(resource.filename).replace(CSV, "").let { UUID.fromString(it) }

            withLoggingContext(MdcKey.TEST_ID to testId) {
                val test = testRepository.findById(testId).orElseThrow()

                logger.debug { "Found test: $test" }

                requireNotNull(test.checkList) {
                    "Checklist for test not found"
                }

                withLoggingContext(MdcKey.TEST_SESSION_ID to test.checkList?.id) {
                    val checklist = test.checkList!!

                    val skills = checklist.relatedSkills.map { it.name }

                    val questionsBySkills = readCSV(resource.inputStream)


                    // FIXME too difficult

                    questionsBySkills
                        .filter { !skills.contains(it.skill) }
                        .groupBy({ it.skill }) { entry ->
                            CheckListQuestionEntity(
                                id = entry.checkListModel.id,
                                question = entry.checkListModel.question,
                                answerOptions = entry.checkListModel.options
                            )
                        }
                        .forEach { skillGroup ->
                            val skillEntity = SkillEntity(name = skillGroup.key)

                            val relatedQuestions = checkListQuestionRepository
                                .saveAll(skillGroup.value)
                                .map {
                                    SkillCheckListQuestion(
                                        skill = skillEntity,
                                        question = it
                                    )
                                }


                            skillEntity.relatedCheckListQuestions.addAll(relatedQuestions)

                            checklist.relatedSkills.add(skillEntity)
                        }

                    checkListRepository.save(checklist)
                }
            }


        }
    }

    private fun readCSV(inputStream: InputStream) = CSVFormat.Builder.create(CSVFormat.DEFAULT).apply {
        setHeader("SKILL", "TRAINEE", "JUNIOR", "MIDDLE", "SENIOR", "SKILL_ID", "QUESTION_ID")
        setIgnoreSurroundingSpaces(true)
        setSkipHeaderRecord(true)
        logger.info { "Parsing skills and questions from resource..." }
    }.build().parse(inputStream.reader())
        .drop(1)
        .map { lineWithHeader ->
            val options = lineWithHeader
                .let { optionsWithHeader ->
                    setOf(
                        TestCheckListQuestionOption(
                            value = optionsWithHeader.get(TestCheckListQuestionOptionRank.TRAINEE.name),
                            rank = TestCheckListQuestionOptionRank.TRAINEE
                        ),
                        TestCheckListQuestionOption(
                            value = optionsWithHeader.get(TestCheckListQuestionOptionRank.JUNIOR.name),
                            rank = TestCheckListQuestionOptionRank.JUNIOR
                        ),
                        TestCheckListQuestionOption(
                            value = optionsWithHeader.get(TestCheckListQuestionOptionRank.MIDDLE.name),
                            rank = TestCheckListQuestionOptionRank.MIDDLE
                        ),
                        TestCheckListQuestionOption(
                            value = optionsWithHeader.get(TestCheckListQuestionOptionRank.SENIOR.name),
                            rank = TestCheckListQuestionOptionRank.SENIOR
                        )
                    )
                }

            val skill = lineWithHeader.get(HEADER_SKILL)

            logger.debug { "Parsed questions by skill: $skill" }

            return@map SkillElement(
                skill = skill,
                checkListModel = UserRelatedTestCheckListModel.CheckListQuestion(
                    id = UUID.fromString(lineWithHeader.get(HEADER_QUESTION_ID)),
                    question = QUESTION_CONTENT_PREFIX.replace("%skill%", skill),
                    options = options
                )
            )
        }
}

class SkillElement(
    val skill: String,
    val checkListModel: UserRelatedTestCheckListModel.CheckListQuestion
)
