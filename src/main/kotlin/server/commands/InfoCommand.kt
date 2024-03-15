package org.example.server.commands

import database.DatabaseInterface
import client.udp.User

class InfoCommand : ServerSideCommand() {
    override suspend fun executeImplementation(
        user: User?,
        database: DatabaseInterface,
        argument: Any?
    ): Result<String> {
        assert(argument == null)
        return Result.success(database.getInfo())
    }
}
