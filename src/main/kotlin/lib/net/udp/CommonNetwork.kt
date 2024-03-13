package lib.net.udp

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lib.json.ObjectMapperWithModules
import java.net.DatagramPacket
import java.net.DatagramSocket

open class CommonNetwork(
    private val socket: DatagramSocket,
    val objectMapperWithModules: ObjectMapperWithModules = ObjectMapperWithModules()
) {
    constructor(port: Int, objectMapperWithModules: ObjectMapperWithModules = ObjectMapperWithModules()) : this(
        DatagramSocket(port),
        objectMapperWithModules
    )

    fun setTimeout(timeout: Int) {
        socket.soTimeout = timeout
    }

    suspend fun send(packet: DatagramPacket, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        withContext(dispatcher) {
            socket.send(packet)
        }
    }

    private suspend fun receive(
        packet: DatagramPacket,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): DatagramPacket {
        withContext(dispatcher) {
            socket.receive(packet)
        }

        return packet
    }

    suspend fun receive(bufferSize: Int = defaultUDPBufferSize, dispatcher: CoroutineDispatcher = Dispatchers.IO): DatagramPacket {
        val buffer = ByteArray(bufferSize)
        val packet = DatagramPacket(buffer, buffer.size)
        return receive(packet, dispatcher)
    }

    suspend fun receiveJson(bufferSize: Int = defaultUDPBufferSize, dispatcher: CoroutineDispatcher = Dispatchers.IO): JsonHolder {
        val packet = receive(bufferSize, dispatcher)
        return packet.constructJsonHolder(objectMapperWithModules.objectMapper)
    }

    fun close() {
        socket.close()
    }
}

