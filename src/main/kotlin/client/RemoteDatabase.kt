package client

import client.udp.ResultFrame
import database.Address
import database.DatabaseInterface
import database.NetworkCode
import database.Organization
import exceptions.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import lib.ExecutionStatus
import network.client.DatabaseCommand
import server.AuthorizationInfo
import java.net.InetSocketAddress

class RemoteDatabase(
    authorizationInfo: AuthorizationInfo,
    address: InetSocketAddress,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : DatabaseInterface {
    private val commandSender = CommandSender(authorizationInfo, address)
    private val databaseScope = CoroutineScope(dispatcher)

    constructor(authorizationInfo: AuthorizationInfo, address: String, port: Int)
            : this(authorizationInfo, InetSocketAddress(address, port))

    private suspend fun sendCommandAndReceiveResult(
        command: DatabaseCommand,
        argument: JsonElement
    ): Result<JsonElement> {
        commandSender.sendCommand(command, argument)
        val json = commandSender.network.receiveStringInPackets()
        val frame = Json.decodeFromJsonElement<ResultFrame>(json.jsonNodeRoot)
        val code = frame.code

        return when (code) {
            NetworkCode.SUCCESS -> Result.success(frame.value)
            NetworkCode.NOT_SUPPOERTED_COMMAND -> Result.failure(CommandNotExistsException())
            NetworkCode.NOT_A_MAXIMUM_ORGANIZATION -> Result.failure(NotMaximumOrganizationException())
            NetworkCode.ORGANIZATION_ALREADY_EXISTS -> Result.failure(OrganizationAlreadyPresentedException())
            NetworkCode.UNABLE_TO_SAVE_TO_FILE -> Result.failure(FileWriteException())
            NetworkCode.UNABLE_TO_READ_FROM_FILE -> Result.failure(FileReadException())
            NetworkCode.NOT_FOUND -> Result.failure(OrganizationNotFoundException())
            NetworkCode.ORGANIZATION_KEY_ERROR -> Result.failure(OrganizationKeyException())
            NetworkCode.INVALID_OUTPUT_FORMAT -> Result.failure(InvalidOutputFormatException())
            NetworkCode.FAILURE -> Result.failure(Exception())
        }
    }

    override suspend fun getInfo(): String {
        val result = sendCommandAndReceiveResult(DatabaseCommand.INFO, Json.encodeToJsonElement(null as Int?))
        result.onFailure { throw it }
        return Json.decodeFromJsonElement(result.getOrNull()!!)
    }

    override suspend fun getSumOfAnnualTurnover(): Double {
        val result =
            sendCommandAndReceiveResult(DatabaseCommand.SUM_OF_ANNUAL_TURNOVER, Json.encodeToJsonElement(null as Int?))
        result.onFailure { throw it }
        return Json.decodeFromJsonElement(result.getOrNull()!!)
    }

    override suspend fun maxByFullName(): Organization? {
        val result =
            sendCommandAndReceiveResult(DatabaseCommand.MAX_BY_FULL_NAME, Json.encodeToJsonElement(null as Int?))

        if (result.isSuccess) {
            return Json.decodeFromJsonElement(result.getOrNull()!!)
        }

        return null
    }

    override suspend fun add(organization: Organization) {
        sendCommandAndReceiveResult(DatabaseCommand.ADD, Json.encodeToJsonElement(organization)).onFailure { throw it }
    }

    override suspend fun addIfMax(newOrganization: Organization): ExecutionStatus {
        val result = sendCommandAndReceiveResult(DatabaseCommand.ADD_IF_MAX, Json.encodeToJsonElement(newOrganization))
        return ExecutionStatus.getByValue(result.isSuccess)
    }

    override suspend fun modifyOrganization(updatedOrganization: Organization) {
        sendCommandAndReceiveResult(
            DatabaseCommand.UPDATE,
            Json.encodeToJsonElement(updatedOrganization)
        ).onFailure { throw it }
    }

    override suspend fun removeById(id: Int): ExecutionStatus {
        val result = sendCommandAndReceiveResult(DatabaseCommand.REMOVE_BY_ID, Json.encodeToJsonElement(id))
        return ExecutionStatus.getByValue(result.isSuccess)
    }

    override suspend fun removeAllByPostalAddress(address: Address) {
        sendCommandAndReceiveResult(
            DatabaseCommand.REMOVE_ALL_BY_POSTAL_ADDRESS,
            Json.encodeToJsonElement(address)
        ).onFailure { throw it }
    }

    override suspend fun removeHead(): Organization? {
        val result = sendCommandAndReceiveResult(DatabaseCommand.REMOVE_HEAD, Json.encodeToJsonElement(null as Int?))

        if (result.isSuccess) {
            return Json.decodeFromJsonElement(result.getOrNull()!!)
        }

        return null
    }

    override suspend fun clear() {
        sendCommandAndReceiveResult(
            DatabaseCommand.CLEAR,
            Json.encodeToJsonElement(null as Int?)
        ).onFailure { throw it }
    }

    override suspend fun save(path: String): Deferred<ExecutionStatus> {
        return databaseScope.async {
            val result = sendCommandAndReceiveResult(DatabaseCommand.SAVE, Json.encodeToJsonElement(path))
            ExecutionStatus.getByValue(result.isSuccess)
        }
    }

    override suspend fun loadFromFile(path: String): ExecutionStatus {
        val result = sendCommandAndReceiveResult(DatabaseCommand.READ, Json.encodeToJsonElement(path))
        return ExecutionStatus.getByValue(result.isSuccess)
    }

    private suspend fun sendShowCommand(format: String): String {
        val result = sendCommandAndReceiveResult(DatabaseCommand.SHOW, Json.encodeToJsonElement(format))
        result.onFailure { throw it }
        return Json.decodeFromJsonElement(result.getOrNull()!!)
    }

    override suspend fun toJson() = sendShowCommand("json")

    override suspend fun toCSV() = sendShowCommand("csv")
}
