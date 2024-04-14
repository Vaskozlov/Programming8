package server

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import lib.net.udp.JsonHolder
import org.example.database.AuthorizationManager
import org.example.database.auth.AuthorizationInfo
import org.example.lib.net.udp.CommandWithArgument
import org.example.lib.net.udp.Frame
import org.example.lib.net.udp.User

abstract class ServerWithAuthorization(
    port: Int,
    commandFieldName: String,
    private val authorizationManager: AuthorizationManager,
) : ServerWithCommands(port, commandFieldName) {

    abstract fun handleAuthorized(
        user: User,
        authorizationInfo: AuthorizationInfo,
        commandWithArgument: CommandWithArgument,
    )

    open fun handleUnauthorized(user: User, commandWithArgument: CommandWithArgument) {
        // do nothing
    }

    private suspend fun registerNewUser(authorizationInfo: AuthorizationInfo, user: User, frame: Frame): Boolean {
        logger.warn("User ${authorizationInfo.login} is not authorized, it will be created")
        val creationResult = authorizationManager.addUser(authorizationInfo)

        if (creationResult.isFailure) {
            logger.warn("Failed to create user ${authorizationInfo.login}")
            handleUnauthorized(user, frame.value)
            return false
        }

        return true
    }

    override suspend fun handlePacket(user: User, jsonHolder: JsonHolder) {
        val frame = Json.decodeFromJsonElement<Frame>(jsonHolder.jsonNodeRoot)
        val authorizationInfo = frame.authorization

        when {
            authorizationManager.isValidUser(authorizationInfo) -> {
                logger.info("Received packet from authorized user: ${authorizationInfo.login}")
            }

            authorizationManager.loginExists(authorizationInfo.login) -> {
                logger.warn("User ${authorizationInfo.login} is not authorized, but it exists")
                handleUnauthorized(user, frame.value)
                return
            }

            else ->
                if (!registerNewUser(authorizationInfo, user, frame)) {
                    return
                }
        }

        handleAuthorized(user, authorizationInfo, frame.value)
    }
}