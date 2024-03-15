package lib.net.udp

import client.udp.User
import java.net.DatagramPacket

fun DatagramPacket.convertToString(): String {
    return String(data, 0, length)
}

fun DatagramPacket.constructUser(): User {
    return User(address, port)
}

fun DatagramPacket.constructJsonHolder(): JsonHolder {
    return JsonHolder(constructUser(), this)
}
