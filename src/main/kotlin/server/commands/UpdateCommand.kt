package org.example.server.commands

import database.Organization
import database.DatabaseInterface
import client.udp.User

class UpdateCommand : ServerSideCommand() {
    override suspend fun executeImplementation(
        user: User?,
        database: DatabaseInterface,
        argument: Any?,
    ): Result<Unit?> {
        database.modifyOrganization(argument as Organization)
        return Result.success(null)
    }
}
