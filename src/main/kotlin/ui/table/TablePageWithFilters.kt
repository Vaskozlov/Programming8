package ui.table

import collection.CollectionInterface
import lib.sortedByUpOrDown
import ui.lib.OrganizationStorage
import ui.lib.Table
import javax.swing.table.DefaultTableModel

abstract class TablePageWithFilters(collection: CollectionInterface) : BasicTablePage() {
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

    final override fun getOrganizationsAsArrayOfStrings(): Array<Array<String?>> {
        return organizationStorage.getFilteredOrganizationAsArrayOfStrings()
    }

    fun addFilter(columnName: String) {
        tableFilter?.let {
            val (prevColumnName, prevFilterId) = it
            tableFilter = columnName to (if (prevColumnName == columnName) prevFilterId + 1 else 1)
        } ?: run {
            tableFilter = columnName to 1
        }

        reload(false)
    }

    init {
        tableModel = DefaultTableModel(getOrganizationsAsArrayOfStrings(), columnNames)
    }
}