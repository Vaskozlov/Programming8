package database

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import exceptions.OrganizationAlreadyPresentedException
import exceptions.OrganizationNotFoundException
import kotlinx.coroutines.*
import lib.CSV.CSVStreamLikeReader
import lib.CSV.CSVStreamWriter
import lib.ExecutionStatus
import lib.IOHelper
import lib.IdFactory
import lib.Localization
import lib.collections.ImmutablePair
import lib.json.ObjectMapperWithModules
import lib.json.prettyWrite
import lib.json.write
import java.io.FileWriter
import java.io.IOException
import java.io.StringWriter
import java.nio.file.Path
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.io.path.absolutePathString
import kotlin.math.max

class LocalDatabase(path: Path, dispatcher: CoroutineDispatcher = Dispatchers.IO) :
    DatabaseInterface {
    private var idFactory = IdFactory(1)
    private val databaseScope = CoroutineScope(dispatcher)

    private val initializationDate: LocalDateTime = LocalDateTime.now()
    private val organizations = LinkedList<Organization>()
    private val storedOrganizations = HashSet<ImmutablePair<String?, OrganizationType?>>()

    init {
        runBlocking { loadFromFile(path.absolutePathString()) }
    }

    override suspend fun getInfo(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        return String.format(
            Localization.get("organization.info_message"),
            formatter.format(initializationDate),
            organizations.size
        )
    }

    override suspend fun maxByFullName(): Organization? =
        organizations.maxByOrNull { it.fullName!! }

    override suspend fun getSumOfAnnualTurnover(): Double =
        organizations.sumOf { it.annualTurnover ?: 0.0 }

    override suspend fun add(organization: Organization) {
        organization.id = organization.id ?: idFactory.nextId
        organization.creationDate = LocalDate.now()

        if (isOrganizationAlreadyPresented(organization)) {
            throw OrganizationAlreadyPresentedException()
        }

        addNoCheck(organization)
    }

    override suspend fun addIfMax(newOrganization: Organization): ExecutionStatus {
        val maxOrganization = organizations.maxByOrNull { it.fullName!! }

        if (maxOrganization == null || maxOrganization.fullName!! < newOrganization.fullName!!) {
            add(newOrganization)
            return ExecutionStatus.SUCCESS
        }

        return ExecutionStatus.FAILURE
    }

    private fun addNoCheck(organization: Organization) {
        organization.validate()
        organizations.add(organization)
        storedOrganizations.add(organization.toPairOfFullNameAndType())
        organizations.sortBy { it.fullName }
    }

    override suspend fun modifyOrganization(updatedOrganization: Organization) {
        val organization = organizations.find { it.id == updatedOrganization.id }

        if (organization == null) {
            throw OrganizationNotFoundException()
        }

        completeModification(organization, updatedOrganization)
    }

    override suspend fun removeAllByPostalAddress(address: Address) {
        organizations
            .filter { it.postalAddress == address }
            .forEach {
                organizations.remove(it)
                storedOrganizations.remove(it.toPairOfFullNameAndType())
            }
    }

    override suspend fun removeById(id: Int): ExecutionStatus {
        val elementRemoved = organizations.removeIf { it.id == id }
        return ExecutionStatus.getByValue(elementRemoved)
    }

    override suspend fun removeHead(): Organization? {

        val removedOrganization = organizations.removeFirstOrNull()

        removedOrganization?.let {
            storedOrganizations.remove(it.toPairOfFullNameAndType())
        }

        return removedOrganization
    }

    override suspend fun clear() {
        organizations.clear()
        storedOrganizations.clear()
    }

    private suspend fun tryToLoadFromFile(filename: String): ExecutionStatus {
        clear()

        val fileContent = IOHelper.readFile(filename) ?: return ExecutionStatus.FAILURE

        var maxId = 0
        val reader = CSVStreamLikeReader(fileContent.substring(fileContent.indexOf('\n') + 1))

        while (!reader.isEndOfStream) {
            if (reader.elementLeftInLine < 10) {
                reader.nextLine()
                continue
            }

            val newOrganization: Organization = organizationFromStream(reader)
            maxId = max(maxId, newOrganization.id!!)
            add(newOrganization)
        }

        idFactory.setValue(maxId + 1)
        return ExecutionStatus.SUCCESS
    }

    override suspend fun loadFromFile(path: String): ExecutionStatus {
        return try {
            tryToLoadFromFile(path)
        } catch (ignored: Exception) {
            println("$ignored.message, $ignored.stackTrace")
            ExecutionStatus.FAILURE
        }
    }

    override suspend fun save(path: String): Deferred<ExecutionStatus> {
        return databaseScope.async {
            tryToWriteToFile(path)
        }
    }

    private fun tryToWriteToFile(path: String): ExecutionStatus {
        return try {
            FileWriter(path).use { file ->
                file.write(formCSV())
                file.flush()
            }

            ExecutionStatus.SUCCESS
        } catch (exception: IOException) {
            ExecutionStatus.FAILURE
        }
    }

    override suspend fun toCSV(): String {
        return formCSV()
    }

    private fun formCSV(): String {
        val stream = CSVStreamWriter(StringWriter())
        try {
            stream.append(CSVHeader.headerAsString)
            stream.newLine()

            organizations.forEach {
                it.writeToStream(stream)
                stream.newLine()
            }

            return stream.writer.toString()
        } catch (exception: IOException) {
            // This should never happen
            return ""
        }
    }

    override suspend fun toYaml(): String {
        val objectMapper = ObjectMapperWithModules(YAMLFactory())
        return objectMapper.write(organizations)
    }

    override suspend fun toJson(): String {
        val objectMapper = ObjectMapperWithModules()
        return objectMapper.prettyWrite(organizations)
    }

    private fun completeModification(organization: Organization, updatedOrganization: Organization) {
        updatedOrganization.fillNullFromAnotherOrganization(organization)

        if (!isModificationLegal(organization, updatedOrganization)) {
            throw OrganizationAlreadyPresentedException()
        }

        addNoCheck(updatedOrganization)
        organizations.remove(organization)
    }

    private fun isModificationLegal(previous: Organization, newVersion: Organization): Boolean {
        if (previous.fullName != newVersion.fullName || previous.type != newVersion.type) {
            return !isOrganizationAlreadyPresented(newVersion)
        }

        return true
    }

    private fun isOrganizationAlreadyPresented(organization: Organization): Boolean {
        return storedOrganizations.contains(organization.toPairOfFullNameAndType())
    }
}
