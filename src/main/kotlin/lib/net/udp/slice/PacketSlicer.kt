package org.example.lib.net.udp.slice

import client.udp.User
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import lib.net.udp.JsonHolder
import org.apache.logging.log4j.kotlin.Logging
import org.example.client.udp.SlicedPacketHeader
import org.example.lib.net.udp.UDPNetwork
import java.net.InetSocketAddress
import kotlin.random.Random

class PacketSlicer(val network: UDPNetwork) : Logging {
    data class UserAndHeader(val user: User, val header: SlicedPacketHeader)

    suspend fun sendStringInPackets(
        data: String,
        address: InetSocketAddress,
        packetSize: Int = 3096
    ) {
        val packetId = Random.nextInt()
        val packetCount = calculatePacketCount(data, packetSize)
        logger.trace("Sending $packetCount packets to $address with packetId $packetId")

        for (i in 0 until packetCount) {
            val start = i * packetSize
            val end = minOf(start + packetSize, data.length)
            val packet = data.substring(start, end)
            val slicedPacketHeader = SlicedPacketHeader(packetCount, i, packet.length, packetId, packet)
            network.send(slicedPacketHeader, address)
        }
    }

    private fun calculatePacketCount(data: String, packetSize: Int): Int = (data.length + packetSize - 1) / packetSize

    suspend fun receiveStringInPackets(): JsonHolder {
        val slicedPacketsFrame = SlicedPacketsFrame()
        val packets = mutableListOf<SlicedPacketHeader>()

        do {
            val (user, packetHeader) = receiveJson()

            if (packetHeader.packetIndex == 1) {
                logger.trace("Receiving ${packetHeader.packagesCount} packets from $user with packetId ${packetHeader.packetId}")
            }

            handlePacket(user, slicedPacketsFrame, packetHeader)
        } while (!slicedPacketsFrame.receivedAllPackets())

        packets.sortBy { it.packetIndex }

        return JsonHolder(
            slicedPacketsFrame.user!!,
            slicedPacketsFrame.joinPacketsData()
        )
    }

    private suspend fun receiveJson(): UserAndHeader {
        val packet = network.receiveJson()
        val json = Json.decodeFromJsonElement<SlicedPacketHeader>(packet.jsonNodeRoot)
        return UserAndHeader(packet.user, json)
    }

    private fun handlePacket(
        user: User,
        slicedPacketsFrame: SlicedPacketsFrame,
        packetHeader: SlicedPacketHeader
    ) {
        slicedPacketsFrame.setPackagesCountIfNull(packetHeader.packagesCount)
        slicedPacketsFrame.setPacketIdIfNull(packetHeader.packetId)
        slicedPacketsFrame.setUserIfNull(user)
        slicedPacketsFrame.addPacket(packetHeader)
    }
}