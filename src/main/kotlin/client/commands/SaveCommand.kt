package org.example.client.commands

import database.DatabaseInterface
import exceptions.FileWriteException
import lib.ExecutionStatus
import org.example.client.commands.core.DatabaseDependantCommand

class SaveCommand(organizationDatabase: DatabaseInterface) :
    DatabaseDependantCommand(organizationDatabase) {
    override suspend fun executeImplementation(argument: Any?): Result<Unit?> {
        assert(argument == null)

        if (database.save("").await() == ExecutionStatus.FAILURE) {
            return Result.failure(FileWriteException())
        }

        return Result.success(null)
    }
}
