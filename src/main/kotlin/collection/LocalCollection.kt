package collection

import exceptions.OrganizationAlreadyPresentedException
import exceptions.OrganizationNotFoundException
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import lib.ExecutionStatus
import lib.IdFactory
import lib.Localization
import org.example.lib.CircledStorage
import lib.valueOrNull
import org.apache.logging.log4j.kotlin.Logging
import org.example.database.CollectionToDatabase
import org.example.database.Database
import org.example.database.auth.AuthorizationInfo
import org.example.lib.getLocalDate
import java.sql.ResultSet
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentSkipListSet

class LocalCollection(private val database: Database) : CollectionInterface, Logging {
    private var idFactory = IdFactory(1)

    private val databaseToCollection = CollectionToDatabase(database)
    private val initializationDate: LocalDateTime = LocalDateTime.now()
    private val history = CircledStorage<String>(11)
    private val organizations = ConcurrentSkipListSet<Organization>()
    private val storedOrganizations =
        ConcurrentSkipListSet<Pair<String?, OrganizationType?>>(
            (java.util.Comparator { o1, o2 ->
                val result = o1.first!!.compareTo(o2.first!!)
                result.takeIf { it != 0 || o1.second == null || o2.second == null }
                    ?: o1.second!!.compareTo(o2.second!!)
            })
        )

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        val prettyJson = Json {
            prettyPrint = true
            prettyPrintIndent = "  "
        }

        const val queryToGetOrganizations = """
        SELECT O.ID,
           O.NAME,
           C.X,
           C.Y,
           O.CREATION_TIME,
           O.ANNUAL_TURNOVER,
           O.FULL_NAME,
           O.EMPLOYEES_COUNT,
           OT.NAME AS ORGANIZATION_TYPE_NAME,
           A.ZIP_CODE,
           L.X,
           L.Y,
           L.Z,
           L.NAME,
           O.CREATOR_ID
        FROM ORGANIZATIONS O
             LEFT JOIN ADDRESS A ON O.POSTAL_ADDRESS_ID = A.ID
             LEFT JOIN COORDINATES C ON O.COORDINATES_ID = C.ID
             LEFT JOIN LOCATION L on A.LOCATION_ID = L.ID
             LEFT JOIN ORGANIZATION_TYPES OT ON OT.ID = O.ORGANIZATION_TYPE_ID;
         """
    }

    init {
        runBlocking {
            loadFromDatabase()
        }
    }

    private fun loadFromDatabase() = runBlocking {
        organizations.clear()
        storedOrganizations.clear()

        for (rawOrganization in database.executeQuery(queryToGetOrganizations)) {
            loadOrganization(rawOrganization)
        }
    }

    private fun loadOrganization(result: ResultSet): Organization {
        val organization = Organization(
            id = result.getInt("ID"),
            name = result.getString("NAME"),
            coordinates = Coordinates(
                x = result.getLong("X"),
                y = result.getLong("Y")
            ),
            creationDate = result.getDate("CREATION_TIME").toLocalDate().toKotlinLocalDate(),
            annualTurnover = result.getDouble("ANNUAL_TURNOVER"),
            fullName = result.getString("FULL_NAME"),
            employeesCount = result.getInt("EMPLOYEES_COUNT"),
            type = valueOrNull<OrganizationType>(result.getString("ORGANIZATION_TYPE_NAME")),
            postalAddress = Address(
                zipCode = result.getString("ZIP_CODE"),
                town = Location(
                    x = result.getDouble("X"),
                    y = result.getFloat("Y"),
                    z = result.getLong("Z"),
                    name = result.getString("NAME")
                )
            ),
            creatorId = result.getInt("CREATOR_ID")
        )

        organization.optimize()
        organizations.add(organization)
        storedOrganizations.add(organization.toPairOfFullNameAndType())

        return organization
    }

    override fun login(authorizationInfo: AuthorizationInfo) {
        // Do nothing
    }

    override fun getInfo(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        return String.format(
            Localization.get("organization.info_message"), formatter.format(initializationDate), organizations.size
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

    private fun addToHistory(message: String) = history.add(getLocalDate().toString() + " " + message)

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

    private fun addNoCheck(organization: Organization): Int {
        organization.validate()
        organizations.add(organization)
        storedOrganizations.add(organization.toPairOfFullNameAndType())
        return databaseToCollection.addOrganization(organization)
    }

    override fun modifyOrganization(updatedOrganization: Organization) {
        addToHistory(Localization.get("command.update") + " " + updatedOrganization.id)
        val organization = organizations.find { it.id == updatedOrganization.id }

        if (organization == null) {
            throw OrganizationNotFoundException()
        }

        if (organization.creatorId != updatedOrganization.creatorId) {
            throw IllegalAccessException()
        }

        completeModification(organization, updatedOrganization)
    }

    override fun removeAllByPostalAddress(address: Address, creatorId: Int?) {
        addToHistory(Localization.get("command.remove_all_by_postal_address") + " " + address.toString())
        organizations
            .filter { it.postalAddress == address && (it.creatorId == creatorId || creatorId == null) }
            .forEach {
                organizations.remove(it)
                storedOrganizations.remove(it.toPairOfFullNameAndType())
                runBlocking {
                    databaseToCollection.removeOrganizationByID(it.id!!)
                }
            }
    }

    override fun removeById(id: Int, creatorId: Int?): ExecutionStatus {
        addToHistory(Localization.get("command.remove_by_id") + " " + id.toString())
        val organizationWithGivenId = organizations.firstOrNull { it.id == id }

        if (organizationWithGivenId == null) {
            return ExecutionStatus.FAILURE
        }

        if (creatorId != null && organizationWithGivenId.creatorId != creatorId) {
            return ExecutionStatus.FAILURE
        }

        organizations.remove(organizationWithGivenId)
        storedOrganizations.remove(organizationWithGivenId.toPairOfFullNameAndType())
        databaseToCollection.removeOrganizationByID(organizationWithGivenId.id!!)

        return ExecutionStatus.SUCCESS
    }

    override fun removeHead(creatorId: Int?): Organization? {
        addToHistory(Localization.get("command.remove_head"))
        val removedOrganization = organizations.firstOrNull { creatorId == null || it.creatorId == creatorId }
        organizations.remove(removedOrganization)

        removedOrganization?.let {
            storedOrganizations.remove(it.toPairOfFullNameAndType())
        }

        return removedOrganization
    }

    private fun clearImplementation(creatorId: Int? = null): Result<Unit> {
        val organizationToRemove = organizations.filter { creatorId == null || it.creatorId == creatorId }

        organizationToRemove.forEach {
            organizations.remove(it)
            storedOrganizations.remove(it.toPairOfFullNameAndType())
            databaseToCollection.removeOrganizationByID(it.id!!)
        }

        return organizationToRemove.takeIf { it.isNotEmpty() }?.let { Result.success(Unit) }
            ?: Result.failure(OrganizationNotFoundException())
    }

    override fun clear(creatorId: Int?): Result<Unit> {
        addToHistory(Localization.get("command.clear"))
        return clearImplementation(creatorId)
    }

    override fun toJson(): String {
        addToHistory(Localization.get("command.show") + " JSON")
        return prettyJson.encodeToString(organizations.toList())
    }

    private fun completeModification(organization: Organization, updatedOrganization: Organization) {
        updatedOrganization.fillNullFromAnotherOrganization(organization)

        if (!isModificationLegal(organization, updatedOrganization)) {
            throw OrganizationAlreadyPresentedException()
        }

        val newId = addNoCheck(updatedOrganization)
        organizations.remove(organization)
        organizations.add(updatedOrganization)
        databaseToCollection.removeOrganizationByID(organization.id!!)
        databaseToCollection.modifyOrganizationId(organization.id!!, newId)
    }

    private fun isModificationLegal(previous: Organization, newVersion: Organization): Boolean {
        if (previous.creatorId == newVersion.creatorId &&
            (previous.fullName != newVersion.fullName || previous.type != newVersion.type)
        ) {
            return !isOrganizationAlreadyPresented(newVersion)
        }

        return true
    }

    private fun isOrganizationAlreadyPresented(organization: Organization): Boolean {
        return storedOrganizations.contains(organization.toPairOfFullNameAndType())
    }
}
