package ui.table

import collection.CollectionInterface
import collection.Organization
import ui.Visualization
import ui.lib.GuiLocalization
import ui.lib.Table
import ui.table.panels.TablePanel

abstract class TablePageWithOrganizationPanels(collection: CollectionInterface) : TablePageWithFilters(collection) {
    internal val tablePanel by lazy { TablePanel(this) }
    private val organizationPanel = tablePanel.organizationPanel
    protected val visualPanel by lazy { Visualization(this) }

    val columnValuesSetters = mapOf(
        Table.NAME_COLUMN to tablePanel::setOrgName,
        Table.COORDINATE_X_COLUMN to tablePanel::setCoordinateX,
        Table.COORDINATE_Y_COLUMN to tablePanel::setCoordinateY,
        Table.ANNUAL_TURNOVER_COLUMN to tablePanel::setAnnualTurnover,
        Table.FULL_NAME_COLUMN to tablePanel::setFullName,
        Table.EMPLOYEES_COUNT_COLUMN to tablePanel::setEmployeesCount,
        Table.TYPE_COLUMN to tablePanel::setType,
        Table.ZIP_CODE_COLUMN to tablePanel::setPostalAddressZipCode,
        Table.LOCATION_X_COLUMN to tablePanel::setPostalAddressTownX,
        Table.LOCATION_Y_COLUMN to tablePanel::setPostalAddressTownY,
        Table.LOCATION_Z_COLUMN to tablePanel::setPostalAddressTownZ,
        Table.LOCATION_NAME_COLUMN to tablePanel::setPostalAddressTownName
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
        GuiLocalization.toInt(
            organizationStorage.getFilteredOrganizationAsArrayOfStrings()[row][Table.ID_COLUMN]
        ) ?: -1
    )

    fun modifyOrganization(organization: Organization) = executeCatching {
        organizationStorage.collection.modifyOrganization(organization)
        requestReload()
    }

    fun getUserId() = organizationStorage.collection.getCreatorId()

    fun clearOrganizations() = executeCatching {
        organizationStorage.collection.clear()
        requestReload()
    }

    fun removeAllByPostalAddress() = executeCatching {
        organizationPanel.getOrganizationAddress()?.let {
            organizationStorage.collection.removeAllByPostalAddress(it)
            requestReload()
        }
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
    }
}