package lib.net.udp

import org.apache.logging.log4j.kotlin.Logging
import org.example.lib.net.udp.User
import org.example.lib.net.udp.slice.PacketSlicer

abstract class Server protected constructor(port: Int) : Logging {
    private val networkInterface = DatagramBasedUDPNetwork(port)
    val network = PacketSlicer(networkInterface)
    private var running = false

    protected abstract fun handlePacket(user: User, jsonHolder: JsonHolder)

    fun run() {
        running = true
        logger.info("Server is running")

        while (running) {
            loopCycle()
        }
    }

    private fun loopCycle() {
        try {
            val packet = network.receiveStringInPackets()
            handlePacket(packet.user, packet)
        } catch (e: Exception) {
            logger.error("Error while receiving packet: $e")
        }
    }
}



