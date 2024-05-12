package client

import collection.Address
import collection.CollectionInterface
import collection.NetworkCode
import collection.Organization
import exceptions.*
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import lib.ExecutionStatus
import lib.net.udp.ResultFrame
import database.auth.AuthorizationInfo
import org.example.exceptions.UnauthorizedException
import java.net.InetSocketAddress
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class RemoteCollection(
    private val address: InetSocketAddress,
) : CollectionInterface {
    companion object {
        private val nullJsonElement = Json.encodeToJsonElement(null as Int?)
    }

    private val lock = ReentrantLock()
    private var userId: Int? = null
    private var commandSender: CommandSender? = null

    constructor(address: String, port: Int)
            : this(InetSocketAddress(address, port))

    override fun login(authorizationInfo: AuthorizationInfo) {
        commandSender = CommandSender(authorizationInfo, address)
    }

    private fun sendCommandAndReceiveResult(
        command: DatabaseCommand,
        argument: JsonElement,
    ): Result<JsonElement> = lock.withLock {
        checkNotNull(commandSender) { "You must login before using the database" }

        commandSender!!.sendCommand(command, argument)
        val json = commandSender!!.network.receiveStringInPackets()
        val frame = Json.decodeFromJsonElement<ResultFrame>(json.jsonNodeRoot)
        val code = frame.code
        userId = frame.userId

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
            NetworkCode.ILLEGAL_ARGUMENTS -> Result.failure(IllegalArgumentsForOrganizationException())
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
        return sendCommandAndReceiveResult(DatabaseCommand.SUM_OF_ANNUAL_TURNOVER, nullJsonElement)
            .onFailure { throw it }
            .let {
                Json.decodeFromJsonElement<Double>(it.getOrNull()!!)
            }
    }

    override fun maxByFullName(): Organization? {
        return sendCommandAndReceiveResult(
            DatabaseCommand.MAX_BY_FULL_NAME,
            nullJsonElement
        ).takeIf { it.isSuccess }
            ?.let {
                Json.decodeFromJsonElement<Organization>(it.getOrNull()!!)
            }
    }

    override fun add(organization: Organization) {
        sendCommandAndReceiveResult(
            DatabaseCommand.ADD,
            Json.encodeToJsonElement(organization)
        ).onFailure { throw it }
    }

    override fun addIfMax(newOrganization: Organization): ExecutionStatus =
        sendCommandAndReceiveResult(
            DatabaseCommand.ADD_IF_MAX,
            Json.encodeToJsonElement(newOrganization)
        ).let { ExecutionStatus.getByValue(it.isSuccess) }


    override fun modifyOrganization(updatedOrganization: Organization) {
        sendCommandAndReceiveResult(
            DatabaseCommand.UPDATE,
            Json.encodeToJsonElement(updatedOrganization)
        ).onFailure { throw it }
    }

    override fun removeById(id: Int, creatorId: Int?): ExecutionStatus =
        sendCommandAndReceiveResult(
            DatabaseCommand.REMOVE_BY_ID,
            Json.encodeToJsonElement(id)
        ).let { ExecutionStatus.getByValue(it.isSuccess) }

    override fun removeAllByPostalAddress(address: Address?, creatorId: Int?) {
        sendCommandAndReceiveResult(
            DatabaseCommand.REMOVE_ALL_BY_POSTAL_ADDRESS,
            Json.encodeToJsonElement(address)
        ).onFailure { throw it }
    }

    override fun removeHead(creatorId: Int?): Organization? =
        sendCommandAndReceiveResult(
            DatabaseCommand.REMOVE_HEAD,
            nullJsonElement
        ).takeIf { it.isSuccess }
            ?.let {
                Json.decodeFromJsonElement<Organization>(it.getOrNull()!!)
            }

    override fun clear(creatorId: Int?): Result<Unit> {
        sendCommandAndReceiveResult(
            DatabaseCommand.CLEAR,
            Json.encodeToJsonElement(null as Int?)
        ).onFailure { throw it }
        return Result.success(Unit)
    }

    private fun sendShowCommand(): String =
        sendCommandAndReceiveResult(
            DatabaseCommand.SHOW,
            nullJsonElement
        ).onFailure { throw it }
            .let {
                Json.decodeFromJsonElement<String>(it.getOrNull()!!)
            }

    override fun toJson() = sendShowCommand()

    override fun getCollection(): List<Organization> =
        sendShowCommand().let {
            Json.decodeFromString(it)
        }

    override fun getLastModificationTime(): LocalDateTime {
        val result = sendCommandAndReceiveResult(
            DatabaseCommand.UPDATE_TIME,
            nullJsonElement
        ).onFailure { throw it }

        return Json.decodeFromJsonElement(result.getOrNull()!!)
    }

    override fun getCreatorId(): Int? = userId
}
