package network.client.udp

import network.client.DatabaseCommand
import server.AuthorizationInfo

data class Frame(val authorization: AuthorizationInfo, val command: DatabaseCommand, val value: Any?)
