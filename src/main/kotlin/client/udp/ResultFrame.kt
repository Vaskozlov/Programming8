package network.client.udp

import database.NetworkCode

data class ResultFrame(var code: NetworkCode = NetworkCode.FAILURE, var value: Any? = null)