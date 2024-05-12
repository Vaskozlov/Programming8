package lib.net.udp

import client.DatabaseCommand
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class CommandWithArgument(val command: DatabaseCommand, val value: JsonElement)
