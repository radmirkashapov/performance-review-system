package dev.rkashapov.security.oauth.api.model

import com.hazelcast.nio.ObjectDataInput
import com.hazelcast.nio.ObjectDataOutput
import com.hazelcast.nio.serialization.DataSerializable
import java.util.*

data class OAuthStateModel(
    var deviceName: String = "Unknown device",
    var deviceId: String = UUID.randomUUID().toString()
) : DataSerializable {
    override fun writeData(out: ObjectDataOutput) {
        out.apply {
            writeString(deviceName)
            writeString(deviceId)
        }

    }

    override fun readData(input: ObjectDataInput) {
        input.apply {
            deviceName = readString()!!
            deviceId = readString()!!
        }

    }
}
