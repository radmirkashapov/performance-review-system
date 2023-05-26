package dev.rkashapov.testing.entity

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.hazelcast.nio.ObjectDataInput
import com.hazelcast.nio.ObjectDataOutput
import com.hazelcast.nio.serialization.DataSerializable
import dev.rkashapov.prs.testing.api.model.CheckListAnswer
import dev.rkashapov.prs.testing.api.model.Skill
import java.util.*

data class PRSStateTestHZModel(
    var userId: UUID = UUID.randomUUID(), // FIXME Classes conforming to DataSerializable should provide a no-arguments constructor
    var testId: UUID = UUID.randomUUID(), // FIXME Classes conforming to DataSerializable should provide a no-arguments constructor
    var checkListAnswersPerSkill: Map<Skill, CheckListAnswer> = emptyMap(),
    var topicMatrixConnectivity: Map<String, List<Double>> = emptyMap()
) : DataSerializable {

    private val objectMapper = jacksonObjectMapper()

    override fun writeData(out: ObjectDataOutput) {
        out.apply {
            writeString(userId.toString())
            writeString(testId.toString())
            writeString(objectMapper.writeValueAsString(checkListAnswersPerSkill))
            writeString(objectMapper.writeValueAsString(topicMatrixConnectivity))
        }
    }

    override fun readData(input: ObjectDataInput) {
        input.apply {
            userId = UUID.fromString(readString())
            testId = UUID.fromString(readString())
            checkListAnswersPerSkill = objectMapper.readValue(readString()!!)
            topicMatrixConnectivity = objectMapper.readValue(readString()!!)
        }

    }
}
