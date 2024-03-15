package org.example.server.commands

import client.udp.User
import database.DatabaseInterface
import exceptions.InvalidOutputFormatException

class ShowCommand : ServerSideCommand() {
    override suspend fun executeImplementation(
        user: User?,
        database: DatabaseInterface,
        argument: Any?
    ): Result<String> {
        return when (argument as String?) {
            "json", null -> Result.success(database.toJson())
            "csv" -> Result.success(database.toCSV())
            else -> Result.failure(InvalidOutputFormatException())
        }
    }
}
