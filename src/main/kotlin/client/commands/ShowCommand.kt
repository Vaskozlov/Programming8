package org.example.client.commands

import database.DatabaseInterface
import exceptions.InvalidOutputFormatException
import org.example.client.commands.core.DatabaseDependantCommand

class ShowCommand(organizationDatabase: DatabaseInterface) : DatabaseDependantCommand(organizationDatabase) {
    override suspend fun executeImplementation(argument: Any?): Result<String> {
        val mode = argument as String?

        return when (mode) {
            "json", null -> Result.success(database.toJson())
            "csv" -> Result.success(database.toCSV())
            else -> Result.failure(InvalidOutputFormatException())
        }
    }
}
