package server

import client.DatabaseCommand
import collection.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import lib.net.udp.ResultFrame
import org.apache.logging.log4j.kotlin.Logging
import org.example.database.AuthorizationManager
import database.Database
import database.auth.AuthorizationInfo
import org.example.lib.net.udp.CommandWithArgument
import org.example.lib.net.udp.User
import java.net.InetSocketAddress
import java.util.concurrent.ForkJoinPool

class CollectionCommandsReceiver(
    port: Int,
    database: Database,
) : Logging,
    ServerWithAuthorization(port, "command", AuthorizationManager(database)) {
    private fun getCreatorId(authorizationInfo: AuthorizationInfo): Int {
        val creatorId: Int

        runBlocking {
            creatorId = authorizationManager.getUserId(authorizationInfo.login)!!
        }

        return creatorId
    }

    private val commandArguments: MutableMap<DatabaseCommand, (AuthorizationInfo, JsonElement) -> Any?> = mutableMapOf(
        DatabaseCommand.ADD to { authorizationInfo, jsonElement ->
            val organization = Json.decodeFromJsonElement<Organization>(
                jsonElement
            )

            organization.creatorId = getCreatorId(authorizationInfo)
            organization
        },

        DatabaseCommand.REMOVE_BY_ID to { authorizationInfo, jsonElement ->
            Json.decodeFromJsonElement<Int>(jsonElement) to getCreatorId(authorizationInfo)
        },
        DatabaseCommand.REMOVE_ALL_BY_POSTAL_ADDRESS to { authorizationInfo, jsonElement ->
            Json.decodeFromJsonElement<Address>(jsonElement) to getCreatorId(authorizationInfo)
        },
        DatabaseCommand.SHOW to { _, _ -> null },
        DatabaseCommand.EXIT to { _, _ -> null },
        DatabaseCommand.UPDATE_TIME to { _, _ -> null },
        DatabaseCommand.CLEAR to { authorizationInfo, _ -> getCreatorId(authorizationInfo) },
        DatabaseCommand.REMOVE_HEAD to { authorizationInfo, _ ->
            getCreatorId(authorizationInfo)
        },
        DatabaseCommand.MAX_BY_FULL_NAME to { _, _ -> null },
        DatabaseCommand.INFO to { _, _ -> null },
        DatabaseCommand.HISTORY to { _, _ -> null },
        DatabaseCommand.SUM_OF_ANNUAL_TURNOVER to { _, _ -> null },
    )

    private val collectionOfOrganizations = LocalCollection(database)

    init {
        commandArguments[DatabaseCommand.ADD]?.let {
            commandArguments[DatabaseCommand.ADD_IF_MAX] = it
        }

        commandArguments[DatabaseCommand.ADD]?.let {
            commandArguments[DatabaseCommand.UPDATE] = it
        }
    }

    private fun execute(
        command: DatabaseCommand,
        user: User,
        database: CollectionInterface,
        argument: Any?,
    ): Result<Any?> {
        return commandMap[command]!!.execute(user, database, argument)
    }

    private fun getArgumentForTheCommand(
        command: DatabaseCommand,
        authorizationInfo: AuthorizationInfo,
        jsonHolder: JsonElement,
    ): Any? {
        return commandArguments[command]?.invoke(authorizationInfo, jsonHolder)
    }

    private fun serialize(value: Any?): JsonElement =
        when (value) {
            null -> Json.encodeToJsonElement(null as Int?)
            is Organization -> Json.encodeToJsonElement(value)
            is Int -> Json.encodeToJsonElement(value)
            is Address -> Json.encodeToJsonElement(value)
            is String -> Json.encodeToJsonElement(value)
            is LocalDateTime -> Json.encodeToJsonElement(value)
            else -> throw IllegalArgumentException("Unknown type")
        }

    private fun send(
        user: User,
        code: NetworkCode,
        data: JsonElement,
    ) {
        val frame = ResultFrame(user.userId, code, data)
        val encodedFrame = Json.encodeToString(frame)
        network.sendStringInPackets(
            encodedFrame,
            InetSocketAddress(user.address, user.port)
        )
    }

    private fun sendResult(
        user: User,
        result: Result<Any?>,
    ) {
        val code = if (result.isSuccess) NetworkCode.SUCCESS else errorToNetworkCode(result.exceptionOrNull())

        ForkJoinPool.commonPool().execute {
            logger.trace("Sending result to $user, code: $code")
        }

        send(user, code, serialize(result.getOrNull()))
    }

    override fun handleUnauthorized(user: User, commandWithArgument: CommandWithArgument) {
        send(user, NetworkCode.UNAUTHORIZED, Json.encodeToJsonElement(""))
    }

    override fun handleAuthorized(
        user: User,
        authorizationInfo: AuthorizationInfo,
        commandWithArgument: CommandWithArgument,
    ) {
        val command = commandWithArgument.command
        logger.trace("Received command $command, from $user")

        if (!commandArguments.containsKey(command)) {
            logger.trace("Command: $command not found, from $user")
            send(user, NetworkCode.NOT_SUPPOERTED_COMMAND, Json.encodeToJsonElement(""))
            return
        }

        logger.trace("Executing command: $command , from $user")
        val commandArgument = getArgumentForTheCommand(command, authorizationInfo, commandWithArgument.value)
        val result = execute(command, user, collectionOfOrganizations, commandArgument)

        ForkJoinPool.commonPool().execute {
            sendResult(user, result)
        }
    }
}
