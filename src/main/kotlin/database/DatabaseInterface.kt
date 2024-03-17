package database

import kotlinx.coroutines.Deferred
import lib.ExecutionStatus
import server.AuthorizationInfo

interface DatabaseInterface {
    fun login(authorizationInfo: AuthorizationInfo)

    suspend fun getInfo(): String

    suspend fun getHistory(): String

    suspend fun getSumOfAnnualTurnover(): Double

    suspend fun maxByFullName(): Organization?

    suspend fun add(organization: Organization)

    suspend fun addIfMax(newOrganization: Organization): ExecutionStatus

    suspend fun modifyOrganization(updatedOrganization: Organization)

    suspend fun removeById(id: Int): ExecutionStatus

    suspend fun removeAllByPostalAddress(address: Address)

    suspend fun removeHead(): Organization?

    suspend fun clear()

    suspend fun save(path: String): Deferred<ExecutionStatus>

    suspend fun loadFromFile(path: String): ExecutionStatus

    suspend fun toJson(): String

    suspend fun toCSV(): String
}
