package server

import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import lib.net.udp.JsonHolder
import lib.net.udp.Server
import kotlin.coroutines.CoroutineContext

abstract class ServerWithCommands(
    port: Int,
    private val commandFieldName: String
) : Server(port) {
    protected fun getCommandFromJson(jsonHolder: JsonHolder): String =
        jsonHolder.jsonNodeRoot.jsonObject[commandFieldName]!!.jsonPrimitive.toString()
}
