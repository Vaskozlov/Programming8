package ui.table

import collection.CollectionInterface
import collection.Organization
import ui.Visualization
import ui.lib.Table
import ui.table.panels.TablePanel

abstract class TablePageWithOrganizationPanels(collection: CollectionInterface) : TablePageWithFilters(collection) {
    internal val tablePanel by lazy { TablePanel(this) }
    private val organizationPanel = tablePanel.organizationPanel
    protected val visualPanel by lazy { Visualization(this) }

    val columnValuesSetters = mapOf(
        1 to tablePanel::setOrgName,
        2 to tablePanel::setCoordinateX,
        3 to tablePanel::setCoordinateY,
        5 to tablePanel::setAnnualTurnover,
        6 to tablePanel::setFullName,
        7 to tablePanel::setEmployeesCount,
        8 to tablePanel::setType,
        9 to tablePanel::setPostalAddressZipCode,
        10 to tablePanel::setPostalAddressTownX,
        11 to tablePanel::setPostalAddressTownY,
        12 to tablePanel::setPostalAddressTownZ,
        13 to tablePanel::setPostalAddressTownName
    )

    fun filterChanged() = executeCatching {
        stringFilter = tablePanel.filter
        reload(false)
    }

    fun removeById(id: Int) = executeCatching {
        organizationStorage.collection.removeById(id)
        requestReload()
    }

    fun getOrganizationById(id: Int) = organizationStorage.getOrganizationById(id)

    fun getOrganizationByRow(row: Int) = organizationStorage.getOrganizationById(
        organizationStorage.getFilteredOrganizationAsArrayOfStrings()[row][Table.ORGANIZATION_ID_COLUMN]?.toIntOrNull()
            ?: -1
    )

    fun modifyOrganization(organization: Organization) = executeCatching{
        organizationStorage.collection.modifyOrganization(organization)
        requestReload()
    }

    fun getUserId() = organizationStorage.collection.getCreatorId()

    fun clearOrganizations() = executeCatching {
        organizationStorage.collection.clear()
        requestReload()
    }

    fun addOrganization() = executeCatching {
        organizationPanel.getOrganization()?.let {
            organizationStorage.collection.add(it)
            requestReload()
        }
    }

    fun addOrganizationIfMax() = executeCatching {
        organizationPanel.getOrganization()?.let {
            organizationStorage.collection.addIfMax(it)
            requestReload()
        }
    }

    fun clearOrganizationAddress() = executeCatching {
        organizationPanel.getOrganization()?.let {
            it.postalAddress = null
            modifyOrganization(it)
        }
    }

    fun selectOrganization(row: Int) {
        runCatching {
            organizationPanel.loadOrganization(
                organizationStorage.getFilteredOrganizationAsArrayOfStrings()[row]
            )
        }
    }

    fun unselectOrganization() {
        table.clearSelection()
        organizationPanel.clearFields()
    }

    init {
        @Suppress("LeakingThis")
        table = Table(tableModel, this)

        table.tableHeader.table.rowHeight = 30
        table.tableHeader.reorderingAllowed = false
    }
}