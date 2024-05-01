package client

import org.example.lib.net.udp.Frame
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import database.auth.AuthorizationInfo
import org.example.lib.net.udp.CommandWithArgument
import org.example.lib.net.udp.ChannelUDPNetwork
import org.example.lib.net.udp.slice.PacketSlicer
import java.net.InetSocketAddress

class CommandSender(
    private val authorizationInfo: AuthorizationInfo,
    private val address: InetSocketAddress,
    useAsyncReceive: Boolean = true
) {
    private val networkInterface = ChannelUDPNetwork()
    val network = PacketSlicer(networkInterface)

    init {
        if (useAsyncReceive) {
            networkInterface.enableAsync()
        } else {
            networkInterface.disableAsync()
        }
    }

    fun sendCommand(command: DatabaseCommand, value: JsonElement) {
        val frame = Frame(authorizationInfo, CommandWithArgument(command, value))
        network.sendStringInPackets(Json.encodeToString(frame), address)
    }
}
