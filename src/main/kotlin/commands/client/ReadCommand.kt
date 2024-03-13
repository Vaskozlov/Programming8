package commands.client

import commands.client.core.DatabaseDependantCommand
import database.DatabaseInterface
import exceptions.FileReadException
import lib.ExecutionStatus

class ReadCommand(organizationDatabase: DatabaseInterface) :
    DatabaseDependantCommand(organizationDatabase) {
    override suspend fun executeImplementation(argument: Any?): Result<Unit?> {
        val filename = argument as String

        if (database.loadFromFile(filename) == ExecutionStatus.FAILURE) {
            return Result.failure(FileReadException(filename))
        }

        return Result.success(null)
    }
}
