package org.example.client.commands

import database.DatabaseInterface
import org.example.client.commands.core.DatabaseDependantCommand

class ClearCommand(organizationDatabase: DatabaseInterface) :
    DatabaseDependantCommand(organizationDatabase) {
    override suspend fun executeImplementation(argument: Any?): Result<Unit?> {
        assert(argument == null)

        database.clear()
        return Result.success(null)
    }
}
