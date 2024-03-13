package lib.net.udp

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import lib.json.write
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

open class Client(private val address: InetAddress, private val port: Int) : CommonNetwork(DatagramSocket()) {
    suspend fun send(data: ByteArray, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        return send(DatagramPacket(data, data.size, address, port), dispatcher)
    }

    suspend inline fun <reified T> send(value: T, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        val json = objectMapperWithModules.write(value)
        send(json.toByteArray(), dispatcher)
    }
}
