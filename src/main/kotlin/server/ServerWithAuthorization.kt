package server

import client.udp.Frame
import client.udp.User
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import lib.net.udp.JsonHolder
import org.example.client.udp.CommandWithArgument
import kotlin.coroutines.CoroutineContext

abstract class ServerWithAuthorization(
    port: Int,
    context: CoroutineContext,
    commandFieldName: String,
    private val authorizationManager: AuthorizationManager
) : ServerWithCommands(port, context, commandFieldName) {

    abstract suspend fun handleAuthorized(
        user: User,
        authorizationInfo: AuthorizationInfo,
        commandWithArgument: CommandWithArgument
    )

    open suspend fun handleUnauthorized(user: User, commandWithArgument: CommandWithArgument) {
        // do nothing
    }

    override suspend fun handlePacket(user: User, jsonHolder: JsonHolder) {
        val frame = Json.decodeFromJsonElement<Frame>(jsonHolder.jsonNodeRoot)
        val authorizationInfo = frame.authorization

        if (authorizationManager.isAuthorized(authorizationInfo)) {
            logger.info("Received packet from authorized user: ${authorizationInfo.login}")
        } else if (authorizationManager.hasLogin(authorizationInfo.login)) {
            logger.warn("User ${authorizationInfo.login} is not authorized, but it exists")
            handleUnauthorized(user, frame.value)
            return
        } else {
            logger.warn("User ${authorizationInfo.login} is not authorized, it will be created")
            authorizationManager.addUser(authorizationInfo)
        }

        handleAuthorized(user, authorizationInfo, frame.value)
    }
}