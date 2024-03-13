package commands.client

import commands.client.core.DatabaseDependantCommand
import database.DatabaseInterface

class ClearCommand(
    organizationDatabase: DatabaseInterface
) : DatabaseDependantCommand(organizationDatabase) {
    override suspend fun executeImplementation(argument: Any?): Result<Unit?> {
        assert(argument == null)

        database.clear()
        return Result.success(null)
    }
}
