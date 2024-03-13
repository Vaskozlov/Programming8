package commands.client

import commands.client.core.DatabaseDependantCommand
import database.DatabaseInterface
import exceptions.FileWriteException
import lib.ExecutionStatus

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
