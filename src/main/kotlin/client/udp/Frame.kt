package client.udp

import kotlinx.serialization.Serializable
import org.example.client.udp.CommandWithArgument
import server.AuthorizationInfo

@Serializable
data class Frame(val authorization: AuthorizationInfo, val value: CommandWithArgument)
