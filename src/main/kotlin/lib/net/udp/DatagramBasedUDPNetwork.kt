package lib.net.udp

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress

open class DatagramBasedUDPNetwork(private val socket: DatagramSocket) :
    UDPNetwork() {

    constructor(port: Int) : this(
        DatagramSocket(port)
    )

    override fun receive(byteArray: ByteArray): DatagramPacket {
        val packet = DatagramPacket(byteArray, byteArray.size)
        socket.receive(packet)
        return packet
    }

    override fun send(data: ByteArray, address: InetSocketAddress) {
        val packet = DatagramPacket(data, data.size, address.address, address.port)
        socket.send(packet)
    }

    override fun close() {
        socket.close()
    }
}

