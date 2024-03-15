package client.udp

import database.NetworkCode
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ResultFrame(var code: NetworkCode = NetworkCode.FAILURE, var value: JsonElement)
