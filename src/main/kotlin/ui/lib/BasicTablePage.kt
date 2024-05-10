package ui.lib

import collection.CollectionInterface
import collection.Coordinates
import collection.Organization
import collection.OrganizationType
import lib.getLocalDate
import lib.sortedByUpOrDown
import ui.TablePage
import javax.swing.JFrame
import javax.swing.JTable
import javax.swing.table.DefaultTableModel

abstract class BasicTablePage(collection: CollectionInterface) : JFrame() {
    companion object {
        val columnNames = arrayOf(
            "ID",
            "Name",
            "Coordinate x",
            "Coordinate y",
            "Creation date",
            "Annual turnover",
            "Full name",
            "Employees count",
            "Type",
            "Zip code",
            "Location x",
            "Location y",
            "Location z",
            "Location name",
            "Creator id"
        )
    }

    private var tableFilter: Pair<String, Int>? = null
        set(value) {
            field = value
            organizationStorage.sortedChanged = true
        }

    protected var stringFilter: Pair<String, String>? = null
        set(value) {
            field = value
            organizationStorage.filterChanged = true
        }

    protected val organizationStorage = OrganizationStorage(collection, {
        var result = it

        tableFilter?.let { tFilter ->
            val (columnName, filterId) = tFilter
            val column = getColumnIndex(table, columnName)

            if (filterId % 3 != 0) {
                result = when (column) {
                    Table.ORGANIZATION_ID_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.id
                        }

                    Table.ORGANIZATION_NAME_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.name
                        }

                    Table.ORGANIZATION_COORDINATE_X_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.coordinates?.x
                        }

                    Table.ORGANIZATION_COORDINATE_Y_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.coordinates?.y
                        }

                    Table.ORGANIZATION_CREATION_DATE_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.creationDate
                        }

                    Table.ORGANIZATION_ANNUAL_TURNOVER_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.annualTurnover
                        }

                    Table.ORGANIZATION_FULL_NAME_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.fullName
                        }

                    Table.ORGANIZATION_EMPLOYEES_COUNT_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.employeesCount
                        }

                    Table.ORGANIZATION_TYPE_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.type
                        }

                    Table.ORGANIZATION_ZIP_CODE_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.postalAddress?.zipCode
                        }

                    Table.ORGANIZATION_LOCATION_X_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.postalAddress?.town?.x
                        }

                    Table.ORGANIZATION_LOCATION_Y_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.postalAddress?.town?.y
                        }

                    Table.ORGANIZATION_LOCATION_Z_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.postalAddress?.town?.z
                        }

                    Table.ORGANIZATION_LOCATION_NAME_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.postalAddress?.town?.name
                        }

                    Table.ORGANIZATION_CREATOR_ID_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.creatorId
                        }

                    else -> result
                }
            }
        }

        result
    })
    {
        var result = it

        stringFilter?.let {
            val (columnName, filter) = stringFilter!!
            val column = getColumnIndex(table, columnName)
            result = result.filter { row -> row[column]!!.contains(filter) }.toList()
        }

        result
    }

    protected var tableModel: DefaultTableModel =
        object :
            DefaultTableModel(organizationStorage.getFilteredOrganizationAsArrayOfStrings(), columnNames) {
            override fun isCellEditable(row: Int, column: Int): Boolean {
                return table.isCellEditable(row, column)
            }
        }

    protected val table by lazy { Table(tableModel, this as TablePage) }

    abstract fun requestReload()
    abstract fun reload(requestFullReload: Boolean)

    private fun getColumnIndex(table: JTable, header: String): Int {
        for (i in 0 until table.columnCount) {
            if (table.getColumnName(i) == header) return i
        }
        return -1
    }

    fun getCurrentPoints() =
        organizationStorage.getFilteredOrganizationAsArrayOfStrings()
            .map {
                PointWithInfo(
                    it[Table.ORGANIZATION_COORDINATE_X_COLUMN]?.toIntOrNull() ?: 0,
                    it[Table.ORGANIZATION_COORDINATE_Y_COLUMN]?.toIntOrNull() ?: 0,
                    it[Table.ORGANIZATION_ID_COLUMN] as String,
                    it
                )
            }.toList()

    fun removeById(id: Int) {
        organizationStorage.collection.removeById(id)
        requestReload()
    }

    fun getOrganizationById(id: Int) = organizationStorage.getOrganizationById(id)

    fun getOrganizationByRow(row: Int) = organizationStorage.getOrganizationById(
        organizationStorage.getFilteredOrganizationAsArrayOfStrings()[row][Table.ORGANIZATION_ID_COLUMN]?.toIntOrNull()
            ?: -1
    )

    fun modifyOrganization(organization: Organization) {
        organizationStorage.collection.modifyOrganization(organization)
        requestReload()
    }

    fun getUserId() = organizationStorage.collection.getCreatorId()

    fun addFilter(columnName: String) {
        tableFilter?.let {
            val (prevColumnName, prevFilterId) = it
            tableFilter = columnName to (if (prevColumnName == columnName) prevFilterId + 1 else 1)
        } ?: run {
            tableFilter = columnName to 1
        }

        reload(false)
    }

    protected fun addOrganization(organization: Organization) {
        organizationStorage.collection.add(organization)
        requestReload()
    }

    open fun addOrganization() {
        val organization = Organization(
            id = 0,
            name = "New organization",
            coordinates = Coordinates(0, 0),
            creationDate = getLocalDate(),
            annualTurnover = 1.0,
            fullName = "New organization",
            employeesCount = 1,
            type = OrganizationType.PUBLIC,
            postalAddress = null,
            creatorId = getUserId()
        )

        addOrganization(organization)
    }

    fun clearOrganizations() {
        organizationStorage.collection.clear()
        requestReload()
    }
}