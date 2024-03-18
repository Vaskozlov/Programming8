package org.example.lib.net.udp.slice

import org.example.lib.net.udp.User

data class SlicedPacketsFrame(
    var packetId: Int? = null,
    var packagesCount: Int? = null,
    var packagesReceived: Int = 0,
    var user: User? = null,
    val packets: MutableList<SlicedPacketHeader> = mutableListOf()
) {
    fun receivedAllPackets(): Boolean = packagesReceived == packagesCount

    fun setPacketIdIfNull(packetId: Int) {
        if (this.packetId == null) {
            this.packetId = packetId
        }
    }

    fun setPackagesCountIfNull(packagesCount: Int) {
        if (this.packagesCount == null) {
            this.packagesCount = packagesCount
        }
    }

    fun setUserIfNull(user: User) {
        if (this.user == null) {
            this.user = user
        }
    }

    fun addPacket(packet: SlicedPacketHeader) {
        packets.add(packet)
        ++packagesReceived
    }

    fun joinPacketsData(): String = packets.joinToString("") { it.data }
}
