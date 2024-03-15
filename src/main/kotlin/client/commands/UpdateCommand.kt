package org.example.client.commands

import database.DatabaseInterface
import database.Organization
import org.example.client.commands.core.DatabaseDependantCommand

class UpdateCommand(organizationDatabase: DatabaseInterface) :
    DatabaseDependantCommand(organizationDatabase) {
    override suspend fun executeImplementation(argument: Any?): Result<Unit?> {
        database.modifyOrganization(argument as Organization)
        return Result.success(null)
    }
}
