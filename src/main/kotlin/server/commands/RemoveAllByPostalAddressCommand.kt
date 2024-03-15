package org.example.server.commands

import database.Address
import database.DatabaseInterface
import client.udp.User

class RemoveAllByPostalAddressCommand : ServerSideCommand() {
    override suspend fun executeImplementation(
        user: User?,
        database: DatabaseInterface,
        argument: Any?
    ): Result<Unit?> {
        database.removeAllByPostalAddress(argument as Address)
        return Result.success(null)
    }
}
