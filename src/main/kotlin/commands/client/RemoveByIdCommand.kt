package commands.client

import commands.client.core.DatabaseDependantCommand
import database.DatabaseInterface
import exceptions.OrganizationKeyException
import lib.ExecutionStatus

class RemoveByIdCommand(
    organizationDatabase: DatabaseInterface
) : DatabaseDependantCommand(organizationDatabase) {
    override suspend fun executeImplementation(argument: Any?): Result<Unit?> {
        if (database.removeById(argument as Int) == ExecutionStatus.FAILURE) {
            return Result.failure(OrganizationKeyException("$argument"))
        }

        return Result.success(null)
    }
}
