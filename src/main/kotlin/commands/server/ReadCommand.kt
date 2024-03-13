package commands.server

import database.DatabaseInterface
import exceptions.FileReadException
import lib.ExecutionStatus
import network.client.udp.User

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
