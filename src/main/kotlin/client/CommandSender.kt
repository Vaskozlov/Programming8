package client

import lib.net.udp.Client
import network.client.DatabaseCommand
import network.client.udp.Frame
import server.AuthorizationInfo
import java.net.InetAddress

class CommandSender(address: InetAddress, port: Int) : Client(address, port) {
    init {
        setTimeout(10000)
    }

    suspend fun sendCommand(command: DatabaseCommand, value: Any?) {
        val frame = Frame(AuthorizationInfo("vaskozlov", "123"), command, value)
        send(frame)
    }
}
