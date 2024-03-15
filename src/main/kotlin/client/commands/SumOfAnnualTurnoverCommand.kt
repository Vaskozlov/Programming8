package org.example.client.commands

import database.DatabaseInterface
import org.example.client.commands.core.DatabaseDependantCommand

class SumOfAnnualTurnoverCommand(organizationDatabase: DatabaseInterface) :
    DatabaseDependantCommand(organizationDatabase) {
    override suspend fun executeImplementation(argument: Any?): Result<Double> {
        assert(argument == null)
        return Result.success(database.getSumOfAnnualTurnover())
    }
}
