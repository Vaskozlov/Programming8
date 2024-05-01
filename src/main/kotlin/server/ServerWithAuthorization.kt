package server

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import lib.net.udp.JsonHolder
import org.example.database.AuthorizationManager
import database.auth.AuthorizationInfo
import org.example.lib.net.udp.CommandWithArgument
import org.example.lib.net.udp.Frame
import org.example.lib.net.udp.User

abstract class ServerWithAuthorization(
    port: Int,
    commandFieldName: String,
    protected val authorizationManager: AuthorizationManager,
) : ServerWithCommands(port, commandFieldName) {

    abstract fun handleAuthorized(
        user: User,
        authorizationInfo: AuthorizationInfo,
        commandWithArgument: CommandWithArgument,
    )

    open fun handleUnauthorized(user: User, commandWithArgument: CommandWithArgument) {
        // do nothing
    }

    private fun registerNewUser(authorizationInfo: AuthorizationInfo, user: User, frame: Frame): Boolean {
        logger.warn("User ${authorizationInfo.login} is not authorized, it will be created")
        val creationResult = authorizationManager.addUser(authorizationInfo)

        if (creationResult.isFailure) {
            logger.warn("Failed to create user ${authorizationInfo.login}")
            handleUnauthorized(user, frame.value)
            return false
        }

        return true
    }

    override fun handlePacket(user: User, jsonHolder: JsonHolder) {
        val frame = Json.decodeFromJsonElement<Frame>(jsonHolder.jsonNodeRoot)
        val authorizationInfo = frame.authorization

        authorizationManager.getUserId(authorizationInfo)?.let {
            logger.info("Received packet from authorized user: ${authorizationInfo.login}")
            user.userId = it
            handleAuthorized(user, authorizationInfo, frame.value)
        } ?: when {
            authorizationManager.loginExists(authorizationInfo.login) -> {
                logger.warn("User ${authorizationInfo.login} is not authorized, but it exists")
                handleUnauthorized(user, frame.value)
            }

            else -> if (registerNewUser(authorizationInfo, user, frame)) {
                handleAuthorized(user, authorizationInfo, frame.value)
            }
        }
    }
}