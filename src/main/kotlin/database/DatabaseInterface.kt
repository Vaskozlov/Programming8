package database

import lib.ExecutionStatus
import server.AuthorizationInfo

interface DatabaseInterface {
    fun login(authorizationInfo: AuthorizationInfo)

    fun getInfo(): String

    fun getHistory(): String

    fun getSumOfAnnualTurnover(): Double

    fun maxByFullName(): Organization?

    fun add(organization: Organization)

    fun addIfMax(newOrganization: Organization): ExecutionStatus

    fun modifyOrganization(updatedOrganization: Organization)

    fun removeById(id: Int): ExecutionStatus

    fun removeAllByPostalAddress(address: Address)

    fun removeHead(): Organization?

    fun clear()

    fun save(path: String): ExecutionStatus

    fun loadFromFile(path: String): ExecutionStatus

    fun toJson(): String

    fun toCSV(): String
}
