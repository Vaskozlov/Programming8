package ui.lib

import collection.CollectionInterface
import collection.Organization
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
                    0 -> result.sortedByUpOrDown(filterId % 3 == 2) { elem -> elem.id }
                    1 -> result.sortedByUpOrDown(filterId % 3 == 2) { elem -> elem.name }
                    2 -> result.sortedByUpOrDown(filterId % 3 == 2) { elem -> elem.coordinates?.x }
                    3 -> result.sortedByUpOrDown(filterId % 3 == 2) { elem -> elem.coordinates?.y }
                    4 -> result.sortedByUpOrDown(filterId % 3 == 2) { elem -> elem.creationDate }
                    5 -> result.sortedByUpOrDown(filterId % 3 == 2) { elem -> elem.annualTurnover }
                    6 -> result.sortedByUpOrDown(filterId % 3 == 2) { elem -> elem.fullName }
                    7 -> result.sortedByUpOrDown(filterId % 3 == 2) { elem -> elem.employeesCount }
                    8 -> result.sortedByUpOrDown(filterId % 3 == 2) { elem -> elem.type }
                    9 -> result.sortedByUpOrDown(filterId % 3 == 2) { elem -> elem.postalAddress?.zipCode }
                    10 -> result.sortedByUpOrDown(filterId % 3 == 2) { elem -> elem.postalAddress?.town?.x }
                    11 -> result.sortedByUpOrDown(filterId % 3 == 2) { elem -> elem.postalAddress?.town?.y }
                    12 -> result.sortedByUpOrDown(filterId % 3 == 2) { elem -> elem.postalAddress?.town?.z }
                    13 -> result.sortedByUpOrDown(filterId % 3 == 2) { elem -> elem.postalAddress?.town?.name }
                    14 -> result.sortedByUpOrDown(filterId % 3 == 2) { elem -> elem.creatorId }
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
                    it[2]?.toIntOrNull() ?: 0,
                    it[3]?.toIntOrNull() ?: 0,
                    it[0] as String,
                    it
                )
            }.toList()

    fun removeById(id: Int) {
        organizationStorage.collection.removeById(id)
        requestReload()
    }

    fun getOrganizationById(id: Int) = organizationStorage.getOrganizationById(id)

    fun getOrganizationByRow(row: Int) = organizationStorage.getOrganizationById(
        organizationStorage.getFilteredOrganizationAsArrayOfStrings()[row][0]?.toIntOrNull() ?: -1
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
}