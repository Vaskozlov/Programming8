package lib.net.udp

import database.NetworkCode
import kotlinx.coroutines.*
import lib.json.write
import network.client.udp.ResultFrame
import network.client.udp.User
import org.apache.logging.log4j.kotlin.Logging
import org.apache.logging.log4j.kotlin.logger
import java.net.DatagramPacket
import kotlin.coroutines.CoroutineContext

abstract class Server protected constructor(port: Int, context: CoroutineContext) : Logging, CommonNetwork(port) {
    private val serverScope = CoroutineScope(context)
    private var running = false

    protected abstract suspend fun handlePacket(user: User, jsonHolder: JsonHolder)

    private suspend fun send(
        user: User,
        frame: ResultFrame,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        val json = objectMapperWithModules.write(frame)
        val bytesToSend = json.toByteArray()
        val packet = DatagramPacket(bytesToSend, bytesToSend.size, user.address, user.port)

        send(packet, dispatcher)
    }

    protected suspend fun <T> send(
        user: User,
        code: NetworkCode,
        value: T?,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        send(user, ResultFrame(code, value), dispatcher)
    }

    fun run() = runBlocking {
        running = true
        logger.info("Server is running")

        while (running) {
            loopCycle(serverScope)
        }
    }

    private suspend fun loopCycle(scope: CoroutineScope) {
        try {
            val packet = receive()

            scope.launch {
                val user = packet.constructUser()
                logger.trace("Handling packet from $user")
                handlePacket(user, packet.constructJsonHolder(objectMapperWithModules.objectMapper))
            }
        } catch (e: Exception) {
            logger.error("Error while receiving packet: $e")
        }
    }
}

