package commands.client

import commands.client.core.DatabaseDependantCommand
import database.Organization
import database.DatabaseInterface
import exceptions.OrganizationNotFoundException

class RemoveHeadCommand(
    organizationDatabase: DatabaseInterface
) : DatabaseDependantCommand(organizationDatabase) {
    override suspend fun executeImplementation(argument: Any?): Result<Organization> {
        assert(argument == null)

        val removedOrganization = database.removeHead()

        if (removedOrganization == null) {
            return Result.failure(OrganizationNotFoundException())
        }

        return Result.success(removedOrganization)
    }
}
