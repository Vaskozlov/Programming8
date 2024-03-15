package org.example.server.commands

import database.DatabaseInterface
import exceptions.FileWriteException
import lib.ExecutionStatus
import client.udp.User

class SaveCommand : ServerSideCommand() {
    override suspend fun executeImplementation(
        user: User?,
        database: DatabaseInterface,
        argument: Any?
    ): Result<Unit?> {
        val filename = argument as String

        if (database.save(filename).await() == ExecutionStatus.FAILURE) {
            return Result.failure(FileWriteException(filename))
        }

        return Result.success(null)
    }
}
