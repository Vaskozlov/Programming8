package ui.lib

import collection.CollectionInterface
import collection.Organization
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class OrganizationStorage(
    internal val collection: CollectionInterface,
    private val filter: (List<Array<String?>>) -> List<Array<String?>>?
) {
    var filterChanged = false
    private val databaseCommunicationLock = ReentrantLock()
    private var organizationTypedArrayCache: List<Array<String?>>? = null
    private var organizationFilteredTypedArrayCache: Array<Array<String?>>? = null
    private var organizationListCache: List<Organization>? = null

    fun clearCache() = databaseCommunicationLock.withLock {
        organizationListCache = null
        organizationTypedArrayCache = null
        organizationFilteredTypedArrayCache = null
    }

    fun getOrganizationsList(): List<Organization> = databaseCommunicationLock.withLock {
        if (organizationListCache != null) {
            return organizationListCache!!
        }

        organizationListCache = collection.getCollection()
        return organizationListCache!!
    }

    fun getOrganizationAsArrayOfStrings(): List<Array<String?>> = databaseCommunicationLock.withLock {
        if (organizationTypedArrayCache != null) {
            return organizationTypedArrayCache!!
        }

        return getOrganizationsList().map {
            arrayOf(
                it.id.toString(),
                it.name,
                it.coordinates?.x.toString(),
                it.coordinates?.y.toString(),
                it.creationDate.toString(),
                it.annualTurnover.toString(),
                it.fullName,
                it.employeesCount.toString(),
                it.type.toString(),
                it.postalAddress?.zipCode ?: "null",
                it.postalAddress?.town?.x.toString(),
                it.postalAddress?.town?.y.toString(),
                it.postalAddress?.town?.z.toString(),
                it.postalAddress?.town?.name,
                it.creatorId.toString()
            )
        }.toList()
    }

    fun getFilteredOrganizationAsArrayOfStrings(): Array<Array<String?>> = databaseCommunicationLock.withLock {
        if (!filterChanged && organizationFilteredTypedArrayCache == null) {
            filterChanged = false
            organizationFilteredTypedArrayCache = filter(getOrganizationAsArrayOfStrings())?.toTypedArray()
        }

        return organizationFilteredTypedArrayCache!!
    }
}