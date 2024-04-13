package database

import exceptions.OrganizationAlreadyPresentedException
import exceptions.OrganizationNotFoundException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import lib.CSV.CSVStreamLikeReader
import lib.CSV.CSVStreamWriter
import lib.ExecutionStatus
import lib.IOHelper
import lib.IdFactory
import lib.Localization
import lib.collections.CircledStorage
import org.apache.logging.log4j.kotlin.Logging
import org.example.lib.getLocalDate
import server.AuthorizationInfo
import java.io.FileWriter
import java.io.IOException
import java.io.StringWriter
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.absolutePathString
import kotlin.math.max

class LocalDatabase(path: Path) :
    DatabaseInterface, Logging {
    private var idFactory = IdFactory(1)

    private val initializationDate: LocalDateTime = LocalDateTime.now()
    private val history = CircledStorage<String>(11)
    private val organizations = mutableListOf<Organization>()
    private val storedOrganizations = mutableSetOf<Pair<String?, OrganizationType?>>()

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        val prettyJson = Json {
            prettyPrint = true
            prettyPrintIndent = "  "
        }
    }

    init {
        loadFromFile(path.absolutePathString())
    }

    override fun login(authorizationInfo: AuthorizationInfo) {
        // Do nothing
    }

    override fun getInfo(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        return String.format(
            Localization.get("organization.info_message"),
            formatter.format(initializationDate),
            organizations.size
        )
    }

    override fun getHistory(): String {
        return StringBuilder().apply {
            history.applyFunctionOnAllElements {
                append(it).append('\n')
                addToHistory(Localization.get("command.history"))
            }
        }.toString()
    }

    private fun addToHistory(message: String) =
        history.add(getLocalDate().toString() + " " + message)

    override fun maxByFullName(): Organization? {
        addToHistory(Localization.get("command.max_by_full_name"))
        return organizations.maxByOrNull { it.fullName!! }
    }

    override fun getSumOfAnnualTurnover(): Double {
        addToHistory(Localization.get("command.sum_of_annual_turnover"))
        return organizations.sumOf { it.annualTurnover ?: 0.0 }
    }

    private fun addImplementation(organization: Organization) {
        organization.id = organization.id ?: idFactory.nextId
        organization.creationDate = getLocalDate()

        if (isOrganizationAlreadyPresented(organization)) {
            throw OrganizationAlreadyPresentedException()
        }

        addNoCheck(organization)
    }

    override fun add(organization: Organization) {
        addToHistory(Localization.get("command.add") + " " + organization.fullName!!)
        addImplementation(organization)
    }

    override fun addIfMax(newOrganization: Organization): ExecutionStatus {
        addToHistory(Localization.get("command.add_if_max") + " " + newOrganization.fullName!!)

        val maxOrganization = organizations.maxByOrNull { it.fullName!! }

        if (maxOrganization == null || maxOrganization.fullName!! < newOrganization.fullName!!) {
            addImplementation(newOrganization)
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

    override fun modifyOrganization(updatedOrganization: Organization) {
        addToHistory(Localization.get("command.update") + " " + updatedOrganization.id)
        val organization = organizations.find { it.id == updatedOrganization.id }

        if (organization == null) {
            throw OrganizationNotFoundException()
        }

        completeModification(organization, updatedOrganization)
    }

    override fun removeAllByPostalAddress(address: Address) {
        addToHistory(Localization.get("command.remove_all_by_postal_address") + " " + address.toString())
        organizations
            .filter { it.postalAddress == address }
            .forEach {
                organizations.remove(it)
                storedOrganizations.remove(it.toPairOfFullNameAndType())
            }
    }

    override fun removeById(id: Int): ExecutionStatus {
        addToHistory(Localization.get("command.remove_by_id") + " " + id.toString())
        val elementRemoved = organizations.removeIf { it.id == id }
        return ExecutionStatus.getByValue(elementRemoved)
    }

    override fun removeHead(): Organization? {
        addToHistory(Localization.get("command.remove_head"))
        val removedOrganization = organizations.removeFirstOrNull()

        removedOrganization?.let {
            storedOrganizations.remove(it.toPairOfFullNameAndType())
        }

        return removedOrganization
    }

    private fun clearImplementation() {
        organizations.clear()
        storedOrganizations.clear()
    }

    override fun clear() {
        addToHistory(Localization.get("command.clear"))
        clearImplementation()
    }

    private fun tryToLoadFromFile(filename: String): ExecutionStatus {
        clearImplementation()

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
            addImplementation(newOrganization)
        }

        idFactory.setValue(maxId + 1)
        return ExecutionStatus.SUCCESS
    }

    override fun loadFromFile(path: String): ExecutionStatus {
        return try {
            tryToLoadFromFile(path)
        } catch (ignored: Exception) {
            println("$ignored.message, $ignored.stackTrace")
            ExecutionStatus.FAILURE
        }
    }

    override fun save(path: String): ExecutionStatus {
        addToHistory(Localization.get("command.save"))
        return tryToWriteToFile(path)

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

    override fun toCSV(): String {
        addToHistory(Localization.get("command.show") + " CSV")
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
            logger.trace("${exception.message} ${exception.stackTrace}")
            return ""
        }
    }

    override fun toJson(): String {
        addToHistory(Localization.get("command.show") + " JSON")
        return prettyJson.encodeToString(organizations)
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
