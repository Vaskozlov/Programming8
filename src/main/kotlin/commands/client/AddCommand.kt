package commands.client

import commands.client.core.DatabaseDependantCommand
import database.Organization
import database.DatabaseInterface

class AddCommand(
    organizationDatabase: DatabaseInterface
) : DatabaseDependantCommand(organizationDatabase) {
    override suspend fun executeImplementation(argument: Any?): Result<Unit?> {
        database.add(argument as Organization)
        return Result.success(null);
    }
}
