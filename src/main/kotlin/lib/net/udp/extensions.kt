package lib.net.udp

import com.fasterxml.jackson.databind.ObjectMapper
import network.client.udp.User
import java.net.DatagramPacket

fun DatagramPacket.convertToString(): String {
    return String(data, 0, length)
}

fun DatagramPacket.constructUser(): User {
    return User(address, port)
}

fun DatagramPacket.constructJsonHolder(objectMapper: ObjectMapper): JsonHolder {
    return JsonHolder(objectMapper, constructUser(), this)
}
