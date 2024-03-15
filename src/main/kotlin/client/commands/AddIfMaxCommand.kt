package org.example.client.commands

import database.DatabaseInterface
import database.Organization
import exceptions.NotMaximumOrganizationException
import lib.ExecutionStatus
import org.example.client.commands.core.DatabaseDependantCommand

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
