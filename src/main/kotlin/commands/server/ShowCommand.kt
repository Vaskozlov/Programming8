package commands.server

import database.DatabaseInterface
import exceptions.InvalidOutputFormatException
import network.client.udp.User

class ShowCommand : ServerSideCommand() {
    override suspend fun executeImplementation(
        user: User?,
        database: DatabaseInterface,
        argument: Any?
    ): Result<String> {
        return when (argument as String?) {
            null, "yaml" -> Result.success(database.toYaml())
            "json" -> Result.success(database.toJson())
            "csv" -> Result.success(database.toCSV())
            else -> Result.failure(InvalidOutputFormatException())
        }
    }
}
