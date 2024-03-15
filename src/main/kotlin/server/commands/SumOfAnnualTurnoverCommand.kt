package org.example.server.commands

import database.DatabaseInterface
import client.udp.User

class SumOfAnnualTurnoverCommand : ServerSideCommand() {
    override suspend fun executeImplementation(
        user: User?,
        database: DatabaseInterface,
        argument: Any?
    ): Result<Double> {
        assert(argument == null)
        return Result.success(database.getSumOfAnnualTurnover())
    }
}
