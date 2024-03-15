package org.example.server.commands

import database.DatabaseInterface
import client.udp.User

class ExitCommand : ServerSideCommand() {
    override suspend fun executeImplementation(
        user: User?,
        database: DatabaseInterface,
        argument: Any?
    ): Result<Unit?> {
        return Result.success(null)
    }
}
