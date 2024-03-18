package org.example.lib.net.udp.udp

import kotlinx.serialization.Serializable
import org.example.lib.net.udp.udp.CommandWithArgument
import server.AuthorizationInfo

@Serializable
data class Frame(val authorization: AuthorizationInfo, val value: CommandWithArgument)
