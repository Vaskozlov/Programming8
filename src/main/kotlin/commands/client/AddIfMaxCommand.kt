package commands.client

import commands.client.core.DatabaseDependantCommand
import database.Organization
import database.DatabaseInterface
import exceptions.NotMaximumOrganizationException
import lib.ExecutionStatus

class AddIfMaxCommand(
    organizationDatabase: DatabaseInterface
) : DatabaseDependantCommand(organizationDatabase) {
    override suspend fun executeImplementation(argument: Any?): Result<Unit?> {
        if (database.addIfMax(argument as Organization) == ExecutionStatus.FAILURE) {
            return Result.failure(NotMaximumOrganizationException());
        }

        return Result.success(null)
    }
}
