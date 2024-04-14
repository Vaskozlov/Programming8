package client

import collection.Address
import collection.CollectionInterface
import collection.NetworkCode
import collection.Organization
import exceptions.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import lib.ExecutionStatus
import network.client.DatabaseCommand
import org.example.database.auth.AuthorizationInfo
import org.example.exceptions.UnauthorizedException
import org.example.lib.net.udp.ResultFrame
import java.net.InetSocketAddress

class RemoteCollection(
    private val address: InetSocketAddress,
) : CollectionInterface {
    companion object {
        private val nullJsonElement = Json.encodeToJsonElement(null as Int?)
    }

    private var commandSender: CommandSender? = null

    constructor(address: String, port: Int)
            : this(InetSocketAddress(address, port))

    override fun login(authorizationInfo: AuthorizationInfo) {
        commandSender = CommandSender(authorizationInfo, address)
    }

    private fun sendCommandAndReceiveResult(
        command: DatabaseCommand,
        argument: JsonElement,
    ): Result<JsonElement> {
        checkNotNull(commandSender) { "You must login before using the database" }

        commandSender!!.sendCommand(command, argument)
        val json = commandSender!!.network.receiveStringInPackets()
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
            NetworkCode.UNAUTHORIZED -> Result.failure(UnauthorizedException())
            NetworkCode.ACCESS_LIMITED -> Result.failure(IllegalAccessException())
            NetworkCode.FAILURE -> Result.failure(Exception())
        }
    }

    override fun getInfo(): String {
        val result = sendCommandAndReceiveResult(DatabaseCommand.INFO, nullJsonElement)
        result.onFailure { throw it }
        return Json.decodeFromJsonElement(result.getOrNull()!!)
    }

    override fun getHistory(): String {
        val result = sendCommandAndReceiveResult(DatabaseCommand.HISTORY, nullJsonElement)
        result.onFailure { throw it }
        return Json.decodeFromJsonElement(result.getOrNull()!!)
    }

    override fun getSumOfAnnualTurnover(): Double {
        val result =
            sendCommandAndReceiveResult(DatabaseCommand.SUM_OF_ANNUAL_TURNOVER, nullJsonElement)
        result.onFailure { throw it }
        return Json.decodeFromJsonElement(result.getOrNull()!!)
    }

    override fun maxByFullName(): Organization? {
        val result =
            sendCommandAndReceiveResult(DatabaseCommand.MAX_BY_FULL_NAME, nullJsonElement)

        if (result.isSuccess) {
            return Json.decodeFromJsonElement(result.getOrNull()!!)
        }

        return null
    }

    override fun add(organization: Organization) {
        sendCommandAndReceiveResult(DatabaseCommand.ADD, Json.encodeToJsonElement(organization)).onFailure { throw it }
    }

    override fun addIfMax(newOrganization: Organization): ExecutionStatus {
        val result = sendCommandAndReceiveResult(DatabaseCommand.ADD_IF_MAX, Json.encodeToJsonElement(newOrganization))
        return ExecutionStatus.getByValue(result.isSuccess)
    }

    override fun modifyOrganization(updatedOrganization: Organization) {
        sendCommandAndReceiveResult(
            DatabaseCommand.UPDATE,
            Json.encodeToJsonElement(updatedOrganization)
        ).onFailure { throw it }
    }

    override fun removeById(id: Int, creatorId: Int?): ExecutionStatus {
        val result = sendCommandAndReceiveResult(DatabaseCommand.REMOVE_BY_ID, Json.encodeToJsonElement(id))
        return ExecutionStatus.getByValue(result.isSuccess)
    }

    override fun removeAllByPostalAddress(address: Address, creatorId: Int?) {
        sendCommandAndReceiveResult(
            DatabaseCommand.REMOVE_ALL_BY_POSTAL_ADDRESS,
            Json.encodeToJsonElement(address)
        ).onFailure { throw it }
    }

    override fun removeHead(creatorId: Int?): Organization? {
        val result = sendCommandAndReceiveResult(DatabaseCommand.REMOVE_HEAD, nullJsonElement)

        if (result.isSuccess) {
            return Json.decodeFromJsonElement(result.getOrNull()!!)
        }

        return null
    }

    override fun clear() {
        sendCommandAndReceiveResult(
            DatabaseCommand.CLEAR,
            Json.encodeToJsonElement(null as Int?)
        ).onFailure { throw it }
    }

    private fun sendShowCommand(): String {
        val result = sendCommandAndReceiveResult(DatabaseCommand.SHOW, nullJsonElement)
        result.onFailure { throw it }
        return Json.decodeFromJsonElement(result.getOrNull()!!)
    }

    override fun toJson() = sendShowCommand()
}
