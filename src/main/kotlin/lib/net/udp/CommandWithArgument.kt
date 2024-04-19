package org.example.lib.net.udp

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import client.DatabaseCommand

@Serializable
data class CommandWithArgument(val command: DatabaseCommand, val value: JsonElement)
