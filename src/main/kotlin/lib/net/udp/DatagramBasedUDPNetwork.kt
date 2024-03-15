package lib.net.udp

import org.example.lib.net.udp.UDPNetwork
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress

open class DatagramBasedUDPNetwork(private val socket: DatagramSocket) :
    UDPNetwork() {

    constructor(port: Int) : this(
        DatagramSocket(port)
    )

    override suspend fun receive(byteArray: ByteArray): DatagramPacket {
        val packet = DatagramPacket(byteArray, byteArray.size)
        socket.receive(packet)
        return packet
    }

    override suspend fun send(byteArray: ByteArray, address: InetSocketAddress) {
        val packet = DatagramPacket(byteArray, byteArray.size, address.address, address.port)
        socket.send(packet)
    }

    override fun close() {
        socket.close()
    }
}

