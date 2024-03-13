package commands.client

import commands.client.core.DatabaseDependantCommand
import database.DatabaseInterface
import exceptions.InvalidOutputFormatException

class ShowCommand(organizationDatabase: DatabaseInterface) :
    DatabaseDependantCommand(organizationDatabase) {
    override suspend fun executeImplementation(argument: Any?): Result<String> {
        val mode = argument as String?

        return when (mode) {
            null, "yaml" -> Result.success(database.toYaml())
            "json" -> Result.success(database.toJson())
            "csv" -> Result.success(database.toCSV())
            else -> Result.failure(InvalidOutputFormatException())
        }
    }
}
