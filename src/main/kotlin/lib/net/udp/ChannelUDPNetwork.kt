package org.example.lib.net.udp

import kotlinx.coroutines.delay
import java.io.IOException
import java.net.DatagramPacket
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel

open class ChannelUDPNetwork(private val delayMS: Long = 100, private val timeWaitingForPacket: Long = 10) :
    UDPNetwork() {
    private val channel = DatagramChannel.open()

    fun enableAsync() {
        channel.configureBlocking(false)
    }

    fun disableAsync() {
        channel.configureBlocking(true)
    }

    override fun close() {
        channel.close()
    }

    override suspend fun receive(byteArray: ByteArray): DatagramPacket {
        val buffer = ByteBuffer.wrap(byteArray)
        var addr: SocketAddress? = null

        for (i in 0 until timeWaitingForPacket) {
            addr = channel.receive(buffer)

            if (addr != null) {
                break
            }

            delay(delayMS)
        }

        if (addr == null) {
            throw IOException("No response")
        }

        return DatagramPacket(byteArray, byteArray.size - buffer.remaining(), addr)
    }

    override suspend fun send(data: ByteArray, address: InetSocketAddress) {
        val buffer = ByteBuffer.wrap(data)
        channel.send(buffer, address)
    }
}