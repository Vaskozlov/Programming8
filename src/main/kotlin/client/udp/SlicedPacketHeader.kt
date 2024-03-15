package org.example.client.udp

import kotlinx.serialization.Serializable

@Serializable
data class SlicedPacketHeader(
    val packagesCount: Int,
    val packetIndex: Int,
    val packetSize: Int,
    val packetId: Int,
    val data: String
)