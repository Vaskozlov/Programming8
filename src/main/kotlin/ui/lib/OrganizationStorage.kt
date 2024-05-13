package ui.lib

import collection.CollectionInterface
import collection.Organization
import kotlinx.datetime.toJavaLocalDate
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class OrganizationStorage(
    internal val collection: CollectionInterface,
    private val sorter: (List<Organization>) -> List<Organization>,
    private val filter: (List<Array<String?>>) -> List<Array<String?>>?
) {
    var sortedChanged = false
        set(value) {
            field = value
            sortedOrganizationListCache = null
            organizationTypedArrayCache = null
            organizationFilteredTypedArrayCache = null
        }
    var filterChanged = false
        set(value) {
            field = value
            organizationTypedArrayCache = null
            organizationFilteredTypedArrayCache = null
        }
    private val databaseCommunicationLock = ReentrantLock()
    private var organizationTypedArrayCache: List<Array<String?>>? = null
    private var organizationFilteredTypedArrayCache: Array<Array<String?>>? = null
    private var organizationListCache: List<Organization>? = null
    private var sortedOrganizationListCache: List<Organization>? = null

    fun clearCache() = databaseCommunicationLock.withLock {
        organizationListCache = null
        sortedOrganizationListCache = null
        organizationTypedArrayCache = null
        organizationFilteredTypedArrayCache = null
    }

    fun getOrganizationById(id: Int?): Organization? =
        getOrganizationsList().firstOrNull { it.id == id }

    private fun getOrganizationsList(): List<Organization> = databaseCommunicationLock.withLock {
        if (organizationListCache != null) {
            return organizationListCache!!
        }

        organizationListCache = collection.getCollection()
        return organizationListCache!!
    }

    private fun getSortedOrganizationsList(): List<Organization> = databaseCommunicationLock.withLock {
        if (sortedChanged || sortedOrganizationListCache == null) {
            sortedOrganizationListCache = sorter(getOrganizationsList())
        }

        return sortedOrganizationListCache!!
    }

    private fun getOrganizationAsArrayOfStrings(): List<Array<String?>> = databaseCommunicationLock.withLock {
        if (organizationTypedArrayCache != null) {
            return organizationTypedArrayCache!!
        }

        return getSortedOrganizationsList().map {
            arrayOf(
                GuiLocalization.format(it.id),
                it.name,
                GuiLocalization.format(it.coordinates?.x),
                GuiLocalization.format(it.coordinates?.y),
                GuiLocalization.format(it.creationDate?.toJavaLocalDate()),
                GuiLocalization.format(it.annualTurnover),
                it.fullName,
                GuiLocalization.format(it.employeesCount),
                GuiLocalization.format(it.type),
                it.postalAddress?.zipCode ?: "null",
                GuiLocalization.format(it.postalAddress?.town?.x),
                GuiLocalization.format(it.postalAddress?.town?.y),
                GuiLocalization.format(it.postalAddress?.town?.z),
                it.postalAddress?.town?.name,
                it.creatorName,
                GuiLocalization.format(it.creatorId)
            )
        }.toList()
    }

    fun getFilteredOrganizationAsArrayOfStrings(): Array<Array<String?>> = databaseCommunicationLock.withLock {
        if (filterChanged || organizationFilteredTypedArrayCache == null) {
            filterChanged = false
            organizationFilteredTypedArrayCache = filter(getOrganizationAsArrayOfStrings())?.toTypedArray()
        }

        return organizationFilteredTypedArrayCache!!
    }
}