package lib.net.udp

import client.udp.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.kotlin.Logging
import org.apache.logging.log4j.kotlin.logger
import org.example.lib.net.udp.slice.PacketSlicer
import kotlin.coroutines.CoroutineContext

abstract class Server protected constructor(port: Int, context: CoroutineContext) : Logging {
    private val networkInterface = DatagramBasedUDPNetwork(port)
    val network = PacketSlicer(networkInterface)
    private val serverScope = CoroutineScope(context)
    private var running = false

    protected abstract suspend fun handlePacket(user: User, jsonHolder: JsonHolder)

    fun run() = runBlocking {
        running = true
        logger.info("Server is running")

        while (running) {
            loopCycle()
        }
    }

    private suspend fun loopCycle() {
        try {
            val packet = network.receiveStringInPackets()

            serverScope.launch {
                handlePacket(packet.user, packet)
            }
        } catch (e: Exception) {
            logger.error("Error while receiving packet: $e")
        }
    }
}



