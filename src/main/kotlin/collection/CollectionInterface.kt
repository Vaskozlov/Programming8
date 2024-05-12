package collection

import database.auth.AuthorizationInfo
import kotlinx.datetime.LocalDateTime
import lib.ExecutionStatus

interface CollectionInterface {
    fun login(authorizationInfo: AuthorizationInfo)

    fun getInfo(): String

    fun getHistory(): String

    fun getSumOfAnnualTurnover(): Double

    fun maxByFullName(): Organization?

    fun add(organization: Organization)

    fun addIfMax(newOrganization: Organization): ExecutionStatus

    fun modifyOrganization(updatedOrganization: Organization)

    fun removeById(id: Int, creatorId: Int? = null): ExecutionStatus

    fun removeAllByPostalAddress(address: Address?, creatorId: Int? = null)

    fun removeHead(creatorId: Int? = null): Organization?

    fun clear(creatorId: Int? = null): Result<Unit>

    fun toJson(): String

    fun getCollection(): List<Organization>

    fun getLastModificationTime(): LocalDateTime

    fun getCreatorId(): Int?
}
