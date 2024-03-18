package lib.net.udp

import org.example.lib.net.udp.User
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import java.net.DatagramPacket

class JsonHolder(val user: User, val jsonNodeRoot: JsonElement) {
    constructor(user: User, json: String)
            : this(user, Json.decodeFromString<JsonElement>(json))

    constructor(user: User, packet: DatagramPacket)
            : this(user, packet.convertToString())

    fun getNode(fieldName: String): JsonElement = jsonNodeRoot.jsonObject[fieldName]!!
}