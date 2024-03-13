package commands.client

import commands.client.core.DatabaseDependantCommand
import database.Organization
import database.DatabaseInterface
import exceptions.OrganizationNotFoundException

class MaxByFullNameCommand(
    organizationDatabase: DatabaseInterface
) : DatabaseDependantCommand(organizationDatabase) {
    override suspend fun executeImplementation(argument: Any?): Result<Organization> {
        val maxOrganization = database.maxByFullName()

        if (maxOrganization == null) {
            return Result.failure(OrganizationNotFoundException())
        }

        return Result.success(maxOrganization)
    }
}
