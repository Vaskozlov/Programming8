package commands.client

import commands.client.core.DatabaseDependantCommand
import database.Organization
import database.DatabaseInterface

class UpdateCommand(
    organizationDatabase: DatabaseInterface
) : DatabaseDependantCommand(organizationDatabase) {
    override suspend fun executeImplementation(argument: Any?): Result<Unit?> {
        database.modifyOrganization(argument as Organization)
        return Result.success(null)
    }
}
