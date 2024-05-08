package lib.net.udp

import database.auth.AuthorizationInfo
import kotlinx.serialization.Serializable

@Serializable
data class Frame(val authorization: AuthorizationInfo, val value: CommandWithArgument)
