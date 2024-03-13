package client

import database.Address
import database.NetworkCode
import database.Organization
import database.DatabaseInterface
import exceptions.*
import kotlinx.coroutines.*
import lib.ExecutionStatus
import lib.json.read
import network.client.DatabaseCommand
import java.net.InetAddress

class RemoteDatabase(address: InetAddress, port: Int, dispatcher: CoroutineDispatcher = Dispatchers.IO) :
    DatabaseInterface {
    private val commandSender = CommandSender(address, port)
    private val databaseScope = CoroutineScope(dispatcher)

    constructor(address: String, port: Int)
            : this(InetAddress.getByName(address), port)

    private suspend fun sendCommandAndReceiveResult(command: DatabaseCommand, argument: Any?): Result<Any?> {
        commandSender.sendCommand(command, argument)
        val json = commandSender.receiveJson()
        val code = NetworkCode.valueOf(json.getNode("code").asText())

        return when (code) {
            NetworkCode.SUCCESS -> Result.success(commandSender.objectMapperWithModules.read(json.getNode("value")))
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
        val result = sendCommandAndReceiveResult(DatabaseCommand.INFO, null)
        result.onFailure { throw it }
        return result.getOrNull()!! as String
    }

    override suspend fun getSumOfAnnualTurnover(): Double {
        val result = sendCommandAndReceiveResult(DatabaseCommand.SUM_OF_ANNUAL_TURNOVER, null)
        result.onFailure { throw it }
        return result.getOrNull()!! as Double
    }

    override suspend fun maxByFullName(): Organization? {
        val result = sendCommandAndReceiveResult(DatabaseCommand.MAX_BY_FULL_NAME, null)

        if (result.isSuccess) {
            return result.getOrNull() as Organization
        }

        return null
    }

    override suspend fun add(organization: Organization) {
        sendCommandAndReceiveResult(DatabaseCommand.ADD, organization).onFailure { throw it }
    }

    override suspend fun addIfMax(newOrganization: Organization): ExecutionStatus {
        val result = sendCommandAndReceiveResult(DatabaseCommand.ADD_IF_MAX, newOrganization)
        return ExecutionStatus.getByValue(result.isSuccess)
    }

    override suspend fun modifyOrganization(updatedOrganization: Organization) {
        sendCommandAndReceiveResult(DatabaseCommand.UPDATE, updatedOrganization).onFailure { throw it }
    }

    override suspend fun removeById(id: Int): ExecutionStatus {
        val result = sendCommandAndReceiveResult(DatabaseCommand.REMOVE_BY_ID, id)
        return ExecutionStatus.getByValue(result.isSuccess)
    }

    override suspend fun removeAllByPostalAddress(address: Address) {
        sendCommandAndReceiveResult(DatabaseCommand.REMOVE_ALL_BY_POSTAL_ADDRESS, address).onFailure { throw it }
    }

    override suspend fun removeHead(): Organization? {
        val result = sendCommandAndReceiveResult(DatabaseCommand.REMOVE_HEAD, null)

        if (result.isSuccess) {
            return result.getOrNull() as Organization
        }

        return null
    }

    override suspend fun clear() {
        sendCommandAndReceiveResult(DatabaseCommand.CLEAR, null).onFailure { throw it }
    }

    override suspend fun save(path: String): Deferred<ExecutionStatus> {
        return databaseScope.async {
            val result = sendCommandAndReceiveResult(DatabaseCommand.SAVE, path)
            ExecutionStatus.getByValue(result.isSuccess)
        }
    }

    override suspend fun loadFromFile(path: String): ExecutionStatus {
        val result = sendCommandAndReceiveResult(DatabaseCommand.READ, path)
        return ExecutionStatus.getByValue(result.isSuccess)
    }

    private suspend fun sendShowCommand(format: String): String {
        val result = sendCommandAndReceiveResult(DatabaseCommand.SHOW, format)
        result.onFailure { throw it }
        return result.getOrNull() as String
    }

    override suspend fun toYaml() = sendShowCommand("yaml")

    override suspend fun toJson() = sendShowCommand("json")

    override suspend fun toCSV() = sendShowCommand("csv")
}
