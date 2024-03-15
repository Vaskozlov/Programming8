package org.example.client.commands

import database.DatabaseInterface
import database.Organization
import org.example.client.commands.core.DatabaseDependantCommand

class AddCommand(organizationDatabase: DatabaseInterface) :
    DatabaseDependantCommand(organizationDatabase) {
    override suspend fun executeImplementation(argument: Any?): Result<Unit?> {
        database.add(argument as Organization)
        return Result.success(null);
    }
}
