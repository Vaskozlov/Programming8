package org.example.lib.net.udp

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import lib.net.udp.JsonHolder
import lib.net.udp.constructJsonHolder
import lib.net.udp.defaultUDPBufferSize
import java.net.DatagramPacket
import java.net.InetSocketAddress

abstract class UDPNetwork {
    abstract fun close()

    abstract suspend fun receive(
        byteArray: ByteArray
    ): DatagramPacket

    private suspend fun receive(
        bufferSize: Int = defaultUDPBufferSize
    ): DatagramPacket {
        return receive(ByteArray(bufferSize))
    }

    suspend fun receiveJson(
        bufferSize: Int = defaultUDPBufferSize
    ): JsonHolder {
        val packet = receive(bufferSize)
        return packet.constructJsonHolder()
    }

    abstract suspend fun send(data: ByteArray, address: InetSocketAddress)

    suspend inline fun <reified T> send(value: T, address: InetSocketAddress) {
        val json = Json.encodeToString(value)
        send(json.toByteArray(), address)
    }
}