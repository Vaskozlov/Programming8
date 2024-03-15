package org.example.client.commands

import database.DatabaseInterface
import exceptions.FileReadException
import lib.ExecutionStatus
import org.example.client.commands.core.DatabaseDependantCommand

class ReadCommand(organizationDatabase: DatabaseInterface) : DatabaseDependantCommand(organizationDatabase) {
    override suspend fun executeImplementation(argument: Any?): Result<Unit?> {
        val filename = argument as String

        if (database.loadFromFile(filename) == ExecutionStatus.FAILURE) {
            return Result.failure(FileReadException(filename))
        }

        return Result.success(null)
    }
}
