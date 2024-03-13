package server

import com.fasterxml.jackson.databind.JsonNode
import database.*
import lib.containsKey
import lib.json.read
import lib.net.udp.JsonHolder
import network.client.DatabaseCommand
import network.client.udp.User
import org.apache.logging.log4j.kotlin.Logging
import java.nio.file.Path
import kotlin.coroutines.CoroutineContext
import kotlin.io.path.absolutePathString

class DatabaseCommandsReceiver(
    port: Int,
    context: CoroutineContext,
    userStoragePath: Path,
    private val databaseStoragePath: Path
) : ServerWithAuthorization(port, context, "command", AuthorizationManager(userStoragePath)), Logging {
    private var usersDatabases: MutableMap<AuthorizationInfo, LocalDatabase> = HashMap()
    private val commandArguments: MutableMap<DatabaseCommand, (AuthorizationInfo, JsonHolder) -> Any?> = mutableMapOf(
        DatabaseCommand.ADD to { _, jsonHolder ->
            objectMapperWithModules.read<Organization>(
                getObjectNode(
                    jsonHolder
                )
            )
        },
        DatabaseCommand.REMOVE_BY_ID to { _, jsonHolder ->
            objectMapperWithModules.read<Int>(
                getObjectNode(
                    jsonHolder
                )
            )
        },
        DatabaseCommand.REMOVE_ALL_BY_POSTAL_ADDRESS to { _, jsonHolder ->
            objectMapperWithModules.read<Address>(
                getObjectNode(
                    jsonHolder
                )
            )
        },
        DatabaseCommand.READ to { _, jsonHolder ->
            objectMapperWithModules.read<String>(
                getObjectNode(
                    jsonHolder
                )
            )
        },
        DatabaseCommand.SAVE to { authorizationHeader, _ ->
            getUserDatabaseFile(authorizationHeader).absolutePathString()
        },
        DatabaseCommand.EXIT to { _, _ -> null },
        DatabaseCommand.CLEAR to { _, _ -> null },
        DatabaseCommand.REMOVE_HEAD to { _, _ -> null },
        DatabaseCommand.MAX_BY_FULL_NAME to { _, _ -> null },
        DatabaseCommand.CLEAR to { _, _ -> null },
        DatabaseCommand.INFO to { _, _ -> null },
        DatabaseCommand.HISTORY to { _, _ -> null },
        DatabaseCommand.SUM_OF_ANNUAL_TURNOVER to { _, _ -> null },
    )

    init {
        commandArguments[DatabaseCommand.ADD]?.let {
            commandArguments[DatabaseCommand.ADD_IF_MAX] = it
        }

        commandArguments[DatabaseCommand.ADD]?.let {
            commandArguments[DatabaseCommand.UPDATE] = it
        }

        commandArguments[DatabaseCommand.READ]?.let {
            commandArguments[DatabaseCommand.SHOW] = it;
        }

        val databaseDir = databaseStoragePath.toFile()
        databaseDir.mkdirs()
        require(databaseDir.isDirectory)
    }

    private suspend fun execute(
        command: DatabaseCommand,
        user: User,
        database: DatabaseInterface,
        argument: Any?
    ): Result<Any?>? {
        return commandMap[command]?.execute(user, database, argument)
    }

    private fun getArgumentForTheCommand(
        command: DatabaseCommand,
        authorizationInfo: AuthorizationInfo,
        jsonHolder: JsonHolder
    ): Any? {
        return commandArguments[command]?.invoke(authorizationInfo, jsonHolder)
    }

    private fun getUserDatabase(authorizationInfo: AuthorizationInfo): DatabaseInterface =
        usersDatabases.getOrPut(
            authorizationInfo
        ) { LocalDatabase(getUserDatabaseFile(authorizationInfo)) }

    private suspend fun sendResult(
        user: User,
        result: Result<Any?>
    ) {
        val code = if (result.isSuccess) NetworkCode.SUCCESS else errorToNetworkCode(result.exceptionOrNull())
        logger.trace("Sending result to $user, code: $code")
        send(user, code, result.getOrNull())
    }

    override suspend fun handleAuthorized(
        user: User,
        authorizationInfo: AuthorizationInfo,
        jsonHolder: JsonHolder
    ) {
        val commandName = getCommandFromJson(jsonHolder)
        val database = getUserDatabase(authorizationInfo)

        if (!containsKey<DatabaseCommand>(commandName)) {
            send(user, NetworkCode.NOT_SUPPOERTED_COMMAND, null)
            return
        }

        val command = DatabaseCommand.valueOf(commandName)
        logger.trace("Received command $command, from $user")

        if (!commandArguments.containsKey(command)) {
            logger.trace("Command: $command not found, from $user")
            send(user, NetworkCode.NOT_SUPPOERTED_COMMAND, null)
            return
        }

        logger.trace("Executing command: $command , from $user")
        val commandArgument = getArgumentForTheCommand(command, authorizationInfo, jsonHolder)
        val result = execute(command, user, database, commandArgument)
        sendResult(user, result!!)
    }

    private fun getObjectNode(jsonHolder: JsonHolder): JsonNode {
        return jsonHolder.getNode("value")
    }

    private fun getUserDatabaseFile(authorizationInfo: AuthorizationInfo): Path {
        return databaseStoragePath.resolve("${authorizationInfo.login}.csv")
    }
}
