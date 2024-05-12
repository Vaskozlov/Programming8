package lib.net.udp.slice

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import lib.net.udp.JsonHolder
import lib.net.udp.UDPNetwork
import lib.net.udp.User
import org.apache.logging.log4j.kotlin.Logging
import java.net.InetSocketAddress
import kotlin.random.Random

class PacketSlicer(val network: UDPNetwork) : Logging {
    fun sendStringInPackets(
        data: String,
        address: InetSocketAddress,
        packetSize: Int = 3096
    ) {
        val packetId = Random.nextInt()
        val packetCount = calculatePacketCount(data, packetSize)

        for (i in 0 until packetCount) {
            val start = i * packetSize
            val end = minOf(start + packetSize, data.length)
            val packet = data.substring(start, end)
            val slicedPacketHeader = SlicedPacketHeader(packetCount, i, packet.length, packetId, packet)
            network.send(slicedPacketHeader, address)
        }
    }

    private fun calculatePacketCount(data: String, packetSize: Int): Int = (data.length + packetSize - 1) / packetSize

    fun receiveStringInPackets(): JsonHolder {
        val slicedPacketsFrame = SlicedPacketsFrame()
        val packets = mutableListOf<SlicedPacketHeader>()

        do {
            val (user, packetHeader) = receiveJson()
            handlePacket(user, slicedPacketsFrame, packetHeader)
        } while (!slicedPacketsFrame.receivedAllPackets())

        packets.sortBy { it.packetIndex }

        return JsonHolder(
            slicedPacketsFrame.user!!,
            slicedPacketsFrame.joinPacketsData()
        )
    }

    private fun receiveJson(): Pair<User, SlicedPacketHeader> {
        val packet = network.receiveJson()
        val json = Json.decodeFromJsonElement<SlicedPacketHeader>(packet.jsonNodeRoot)
        return packet.user to json
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