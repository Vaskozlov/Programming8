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
                    Table.ID_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.id
                        }
                    
                    Table.NAME_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.name
                        }
                    
                    Table.COORDINATE_X_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.coordinates?.x
                        }
                    
                    Table.COORDINATE_Y_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.coordinates?.y
                        }
                    
                    Table.CREATION_DATE_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.creationDate
                        }
                    
                    Table.ANNUAL_TURNOVER_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.annualTurnover
                        }
                    
                    Table.FULL_NAME_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.fullName
                        }
                    
                    Table.EMPLOYEES_COUNT_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.employeesCount
                        }
                    
                    Table.TYPE_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.type
                        }
                    
                    Table.ZIP_CODE_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.postalAddress?.zipCode
                        }
                    
                    Table.LOCATION_X_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.postalAddress?.town?.x
                        }
                    
                    Table.LOCATION_Y_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.postalAddress?.town?.y
                        }
                    
                    Table.LOCATION_Z_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.postalAddress?.town?.z
                        }
                    
                    Table.LOCATION_NAME_COLUMN ->
                        result.sortedByUpOrDown(filterId % 3 == 2) { elem ->
                            elem.postalAddress?.town?.name
                        }
                    
                    Table.CREATOR_ID_COLUMN ->
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
        
        runCatching {
            stringFilter?.let {
                val (columnName, filter) = stringFilter!!
                val column = getColumnIndex(table, columnName)
                result = result.filter { row -> row[column]!!.contains(filter) }.toList()
            }
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