package org.example.client.commands

import database.DatabaseInterface
import org.example.client.commands.core.DatabaseDependantCommand

class InfoCommand(organizationDatabase: DatabaseInterface) : DatabaseDependantCommand(organizationDatabase) {
    override suspend fun executeImplementation(argument: Any?): Result<String> {
        return Result.success(database.getInfo())
    }
}
