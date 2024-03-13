package server

import lib.json.read
import lib.net.udp.JsonHolder
import network.client.udp.User
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
        jsonHolder: JsonHolder
    )

    override suspend fun handlePacket(user: User, jsonHolder: JsonHolder) {
        val authorizationInfo = objectMapperWithModules.read<AuthorizationInfo>(jsonHolder.getNode("authorization"))

        if (!authorizationManager.isAuthorized(authorizationInfo)) {
            logger.warn("User $user is not authorized, it will be created")
            authorizationManager.addUser(authorizationInfo)
        } else {
            logger.info("Received packet from authorized user: ${authorizationInfo.login}")
        }

        handleAuthorized(user, authorizationInfo, jsonHolder)
    }
}