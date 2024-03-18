package server

import org.example.lib.net.udp.udp.ResultFrame
import org.example.lib.net.udp.udp.User
import database.*
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import network.client.DatabaseCommand
import org.apache.logging.log4j.kotlin.Logging
import org.example.lib.net.udp.udp.CommandWithArgument
import java.io.Closeable
import java.net.InetSocketAddress
import java.nio.file.Path
import kotlin.coroutines.CoroutineContext
import kotlin.io.path.absolutePathString

class DatabaseCommandsReceiver(
    port: Int,
    context: CoroutineContext,
    userStoragePath: Path,
    private val databaseStoragePath: Path
) : Logging,
    Closeable,
    ServerWithAuthorization(port, context, "command", AuthorizationManager(userStoragePath)) {
    private var usersDatabases: MutableMap<AuthorizationInfo, LocalDatabase> = HashMap()
    private val commandArguments: MutableMap<DatabaseCommand, (AuthorizationInfo, JsonElement) -> Any?> = mutableMapOf(
        DatabaseCommand.ADD to { _, jsonElement ->
            Json.decodeFromJsonElement<Organization>(
                jsonElement
            )
        },
        DatabaseCommand.REMOVE_BY_ID to { _, jsonElement ->
            Json.decodeFromJsonElement<Int>(
                jsonElement
            )
        },
        DatabaseCommand.REMOVE_ALL_BY_POSTAL_ADDRESS to { _, jsonElement ->
            Json.decodeFromJsonElement<Address>(
                jsonElement
            )
        },
        DatabaseCommand.READ to { _, jsonElement ->
            Json.decodeFromJsonElement<String>(
                jsonElement
            )
        },
        DatabaseCommand.SAVE to { authorizationInfo, _ ->
            getUserDatabaseFile(authorizationInfo).absolutePathString()
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
    ): Result<Any?> {
        return commandMap[command]!!
            .execute(user, database, argument)
            .onFailure { Result.failure<Any?>(it) }
    }

    private fun getArgumentForTheCommand(
        command: DatabaseCommand,
        authorizationInfo: AuthorizationInfo,
        jsonHolder: JsonElement
    ): Any? {
        return commandArguments[command]?.invoke(authorizationInfo, jsonHolder)
    }

    private fun getUserDatabase(authorizationInfo: AuthorizationInfo): DatabaseInterface =
        usersDatabases.getOrPut(
            authorizationInfo
        ) { LocalDatabase(getUserDatabaseFile(authorizationInfo)) }

    // reified
    private fun serialize(value: Any?): JsonElement =
        when (value) {
            is Organization -> Json.encodeToJsonElement(value)
            is Int -> Json.encodeToJsonElement(value)
            is Address -> Json.encodeToJsonElement(value)
            is String -> Json.encodeToJsonElement(value)
            else -> Json.encodeToJsonElement(null as Int?)
        }

    private suspend fun send(
        user: User,
        code: NetworkCode,
        data: JsonElement
    ) {
        val frame = ResultFrame(code, data)
        val encodedFrame = Json.encodeToString(frame)
        network.sendStringInPackets(
            encodedFrame,
            InetSocketAddress(user.address, user.port)
        )
    }

    private suspend fun sendResult(
        user: User,
        result: Result<Any?>
    ) {
        val code = if (result.isSuccess) NetworkCode.SUCCESS else errorToNetworkCode(result.exceptionOrNull())
        logger.trace("Sending result to $user, code: $code")
        send(user, code, serialize(result.getOrNull()))
    }


    override suspend fun handleUnauthorized(user: User, commandWithArgument: CommandWithArgument) {
        send(user, NetworkCode.UNAUTHORIZED, Json.encodeToJsonElement(""))
    }

    override suspend fun handleAuthorized(
        user: User,
        authorizationInfo: AuthorizationInfo,
        commandWithArgument: CommandWithArgument
    ) {
        val command = commandWithArgument.command
        val database = getUserDatabase(authorizationInfo)
        logger.trace("Received command $command, from $user")

        if (!commandArguments.containsKey(command)) {
            logger.trace("Command: $command not found, from $user")
            send(user, NetworkCode.NOT_SUPPOERTED_COMMAND, Json.encodeToJsonElement(""))
            return
        }

        logger.trace("Executing command: $command , from $user")
        val commandArgument = getArgumentForTheCommand(command, authorizationInfo, commandWithArgument.value)
        val result = execute(command, user, database, commandArgument)
        sendResult(user, result!!)
    }

    private fun getUserDatabaseFile(authorizationInfo: AuthorizationInfo): Path {
        return databaseStoragePath.resolve("${authorizationInfo.login}.csv")
    }

    override fun close() {
        runBlocking {
            val savingQueue = usersDatabases.map { (authorizationInfo, database) ->
                database.save(getUserDatabaseFile(authorizationInfo).absolutePathString())
            }

            savingQueue.awaitAll()
        }
    }
}
