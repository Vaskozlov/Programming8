package org.example.lib.net.udp

import kotlinx.serialization.Serializable
import database.auth.AuthorizationInfo
import lib.net.udp.CommandWithArgument

@Serializable
data class Frame(val authorization: AuthorizationInfo, val value: CommandWithArgument)
