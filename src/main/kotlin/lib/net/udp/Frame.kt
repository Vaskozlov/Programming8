package org.example.lib.net.udp

import kotlinx.serialization.Serializable
import server.AuthorizationInfo

@Serializable
data class Frame(val authorization: AuthorizationInfo, val value: CommandWithArgument)
