package collection

import database.CollectionToDatabase
import database.Database
import database.auth.AuthorizationInfo
import exceptions.OrganizationAlreadyPresentedException
import exceptions.OrganizationNotFoundException
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import lib.*
import org.apache.logging.log4j.kotlin.Logging
import lib.CircledStorage
import java.sql.ResultSet
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class LocalCollection(private val database: Database) : CollectionInterface, Logging {
    private var idFactory = IdFactory(1)
    private val lock = ReentrantLock()
    private var lastUpdateTime = getLocalDateTime()
    private val databaseToCollection = CollectionToDatabase(database)
    private val initializationDate: LocalDateTime = getLocalDateTime()
    private val history = CircledStorage<String>(11)
    private val organizations = mutableListOf<Organization>()
    private val storedOrganizations =
        mutableListOf<Pair<String?, OrganizationType?>>()

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        val prettyJson = Json {
            prettyPrint = true
            prettyPrintIndent = "  "
        }

        const val QUERY_TO_GET_ORGANIZATIONS = """
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
           L.X AS LX,
           L.Y AS LY,
           L.Z AS LZ,
           L.NAME AS LNAME,
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

    private fun updateModificationTime() {
        lastUpdateTime = getLocalDateTime()
    }

    private fun loadFromDatabase() = lock.withLock {
        runBlocking {
            organizations.clear()
            storedOrganizations.clear()

            for (rawOrganization in database.executeQuery(QUERY_TO_GET_ORGANIZATIONS)) {
                loadOrganization(rawOrganization)
            }
        }
    }

    private fun loadOrganization(result: ResultSet): Organization = lock.withLock {
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
            employeesCount = result.getObject("EMPLOYEES_COUNT") as? Int,
            type = valueOrNull<OrganizationType>(result.getString("ORGANIZATION_TYPE_NAME")),
            postalAddress = Address(
                zipCode = (result.getObject("ZIP_CODE") as?  String).takeIf { it == null || it.isNotEmpty() },
                town = Location(
                    x = result.getObject("LX") as? Double,
                    y = result.getObject("LY") as? Float,
                    z = result.getObject("LZ") as? Long,
                    name = result.getObject("LNAME") as? String
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

        return String.format(
            Localization.get("organization.info_message"), initializationDate.toString(), organizations.size
        )
    }

    override fun getHistory(): String = lock.withLock {
        return StringBuilder().apply {
            history.applyFunctionOnAllElements {
                append(it).append('\n')
                addToHistory(Localization.get("command.history"))
            }
        }.toString()
    }

    private fun addToHistory(message: String) = lock.withLock {
        history.add(getLocalDate().toString() + " " + message)
    }

    override fun maxByFullName(): Organization? = lock.withLock {
        addToHistory(Localization.get("command.max_by_full_name"))
        return organizations.maxByOrNull { it.fullName!! }
    }

    override fun getSumOfAnnualTurnover(): Double = lock.withLock {
        addToHistory(Localization.get("command.sum_of_annual_turnover"))
        return organizations.sumOf { it.annualTurnover ?: 0.0 }
    }

    private fun addImplementation(organization: Organization): Int = lock.withLock {
        organization.id = organization.id ?: idFactory.nextId
        organization.creationDate = getLocalDate()

        if (isOrganizationAlreadyPresented(organization)) {
            throw OrganizationAlreadyPresentedException()
        }

        addNoCheck(organization)
    }

    override fun add(organization: Organization): Unit = lock.withLock {
        addToHistory(Localization.get("command.add") + " " + organization.fullName!!)
        addImplementation(organization)
    }

    override fun addIfMax(newOrganization: Organization): ExecutionStatus = lock.withLock {
        addToHistory(Localization.get("command.add_if_max") + " " + newOrganization.fullName!!)

        val maxOrganization = organizations.maxByOrNull { it.fullName!! }

        if (maxOrganization == null || maxOrganization.fullName!! < newOrganization.fullName!!) {
            addImplementation(newOrganization)
            return ExecutionStatus.SUCCESS
        }

        return ExecutionStatus.FAILURE
    }

    private fun addNoCheck(organization: Organization): Int = lock.withLock {
        organization.validate()
        val realId = databaseToCollection.addOrganization(organization)
        organization.id = realId
        organizations.add(organization)
        storedOrganizations.add(organization.toPairOfFullNameAndType())
        updateModificationTime()
        realId
    }

    override fun modifyOrganization(updatedOrganization: Organization) = lock.withLock {
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

    override fun removeAllByPostalAddress(address: Address, creatorId: Int?) = lock.withLock {
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

        updateModificationTime()
    }

    override fun removeById(id: Int, creatorId: Int?): ExecutionStatus = lock.withLock {
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
        updateModificationTime()

        ExecutionStatus.SUCCESS
    }

    override fun removeHead(creatorId: Int?): Organization? = lock.withLock {
        addToHistory(Localization.get("command.remove_head"))
        val removedOrganization = organizations.firstOrNull { creatorId == null || it.creatorId == creatorId }
        organizations.remove(removedOrganization)

        removedOrganization?.let {
            storedOrganizations.remove(it.toPairOfFullNameAndType())
        }

        removedOrganization
    }

    private fun clearImplementation(creatorId: Int? = null): Result<Unit> = lock.withLock {
        val organizationToRemove = organizations.filter { creatorId == null || it.creatorId == creatorId }

        organizationToRemove.forEach {
            organizations.remove(it)
            storedOrganizations.remove(it.toPairOfFullNameAndType())
            databaseToCollection.removeOrganizationByID(it.id!!)
        }

        organizationToRemove.takeIf { it.isNotEmpty() }?.let { Result.success(Unit) }
            ?: Result.failure(OrganizationNotFoundException())
    }

    override fun clear(creatorId: Int?): Result<Unit> {
        addToHistory(Localization.get("command.clear"))
        return clearImplementation(creatorId)
    }

    override fun toJson(): String = lock.withLock {
        addToHistory(Localization.get("command.show") + " JSON")
        prettyJson.encodeToString(organizations.toList())
    }

    override fun getCollection(): List<Organization> = lock.withLock {
        organizations.toList()
    }

    override fun getLastModificationTime(): LocalDateTime = lock.withLock {
        return lastUpdateTime
    }

    private fun completeModification(organization: Organization, updatedOrganization: Organization) = lock.withLock {
        if (!isModificationLegal(organization, updatedOrganization)) {
            throw OrganizationAlreadyPresentedException()
        }

        updatedOrganization.id = organization.id
        organizations[organizations.indexOf(organization)] = updatedOrganization
        storedOrganizations[storedOrganizations.indexOf(organization.toPairOfFullNameAndType())] =
            updatedOrganization.toPairOfFullNameAndType()

        databaseToCollection.modifyOrganization(updatedOrganization)

        updateModificationTime()
    }

    private fun isModificationLegal(previous: Organization, newVersion: Organization): Boolean {
        if (previous.creatorId == newVersion.creatorId &&
            (previous.fullName != newVersion.fullName || previous.type != newVersion.type)
        ) {
            return !isOrganizationAlreadyPresented(newVersion)
        }

        return true
    }

    private fun isOrganizationAlreadyPresented(organization: Organization): Boolean = lock.withLock {
        return storedOrganizations.contains(organization.toPairOfFullNameAndType())
    }

    override fun getCreatorId(): Int? = null
}
