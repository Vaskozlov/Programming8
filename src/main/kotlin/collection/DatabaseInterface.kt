package collection

import lib.ExecutionStatus
import org.example.database.auth.AuthorizationInfo

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

    fun toJson(): String
}
