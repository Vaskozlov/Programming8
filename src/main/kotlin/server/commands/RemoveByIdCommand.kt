package org.example.server.commands

import database.DatabaseInterface
import client.udp.User

class RemoveByIdCommand : ServerSideCommand() {
    override suspend fun executeImplementation(
        user: User?,
        database: DatabaseInterface,
        argument: Any?
    ): Result<Unit?> {
        database.removeById(argument as Int)
        return Result.success(null)
    }
}
