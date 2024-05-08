package lib.net.udp

import org.apache.logging.log4j.kotlin.Logging
import lib.net.udp.slice.PacketSlicer
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

abstract class Server protected constructor(port: Int) : Logging {
    private val networkInterface = DatagramBasedUDPNetwork(port)
    val network = PacketSlicer(networkInterface)
    private var running = AtomicBoolean(false)
    private val cachedPool = ThreadPoolExecutor(
        0,
        2,
        60L,
        TimeUnit.SECONDS,
        SynchronousQueue()
    )

    protected abstract fun handlePacket(user: User, jsonHolder: JsonHolder)

    private fun loop()
    {
        while (running.get()) {
            loopCycle()
        }
    }

    fun run() {
        running.set(true)
        logger.info("Server is running")

        cachedPool.submit {
            loop()
        }
    }

    private fun loopCycle() {
        try {
            val packet = network.receiveStringInPackets()
            Thread { handlePacket(packet.user, packet) }.start()
        } catch (e: Exception) {
            logger.error("Error while receiving packet: $e")
        }
    }
}



