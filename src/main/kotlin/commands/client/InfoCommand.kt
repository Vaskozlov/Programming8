package commands.client

import commands.client.core.DatabaseDependantCommand
import database.DatabaseInterface

class InfoCommand(organizationDatabase: DatabaseInterface) :
    DatabaseDependantCommand(organizationDatabase) {
    override suspend fun executeImplementation(argument: Any?): Result<String> {
        return Result.success(database.getInfo())
    }
}
