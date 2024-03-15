package org.example.client.commands

import database.DatabaseInterface
import exceptions.OrganizationKeyException
import lib.ExecutionStatus
import org.example.client.commands.core.DatabaseDependantCommand

class RemoveByIdCommand(organizationDatabase: DatabaseInterface) :
    DatabaseDependantCommand(organizationDatabase) {
    override suspend fun executeImplementation(argument: Any?): Result<Unit?> {
        if (database.removeById(argument as Int) == ExecutionStatus.FAILURE) {
            return Result.failure(OrganizationKeyException("$argument"))
        }

        return Result.success(null)
    }
}
