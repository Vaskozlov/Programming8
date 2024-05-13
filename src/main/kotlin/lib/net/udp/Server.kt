package lib.net.udp

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import lib.net.udp.slice.PacketSlicer
import org.apache.logging.log4j.kotlin.Logging
import java.util.concurrent.atomic.AtomicBoolean

abstract class Server protected constructor(port: Int) : Logging {
    private val networkInterface = DatagramBasedUDPNetwork(port)
    val network = PacketSlicer(networkInterface)
    protected val serverScope = CoroutineScope(Dispatchers.Default)
    private var running = AtomicBoolean(false)

    protected abstract fun handlePacket(user: User, jsonHolder: JsonHolder)

    private fun loop() {
        while (running.get()) {
            loopCycle()
        }
    }

    fun run() {
        running.set(true)
        logger.info("Server is running")

        loop()
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



