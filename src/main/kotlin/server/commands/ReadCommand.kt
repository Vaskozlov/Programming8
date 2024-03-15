package org.example.server.commands

import database.DatabaseInterface
import exceptions.FileReadException
import lib.ExecutionStatus
import client.udp.User

class ReadCommand : ServerSideCommand() {
    override suspend fun executeImplementation(
        user: User?,
        database: DatabaseInterface,
        argument: Any?
    ): Result<Unit?> {
        val filename = argument as String

        if (database.loadFromFile(filename) == ExecutionStatus.FAILURE) {
            return Result.failure(FileReadException(filename))
        }

        return Result.success(null)
    }
}
