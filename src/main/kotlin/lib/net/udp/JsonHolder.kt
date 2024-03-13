package lib.net.udp

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import network.client.udp.User
import java.net.DatagramPacket

class JsonHolder(val user: User, val jsonNodeRoot: JsonNode) {
    constructor(objectMapper: ObjectMapper, user: User, json: String)
            : this(user, objectMapper.readTree(json))

    constructor(objectMapper: ObjectMapper, user: User, packet: DatagramPacket)
            : this(objectMapper, user, packet.convertToString())

    fun getNode(fieldName: String): JsonNode = jsonNodeRoot[fieldName]
}