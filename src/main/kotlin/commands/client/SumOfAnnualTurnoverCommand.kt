package commands.client

import commands.client.core.DatabaseDependantCommand
import database.DatabaseInterface

class SumOfAnnualTurnoverCommand(
    organizationDatabase: DatabaseInterface
) : DatabaseDependantCommand(organizationDatabase) {
    override suspend fun executeImplementation(argument: Any?): Result<Double> {
        assert(argument == null)
        return Result.success(database.getSumOfAnnualTurnover())
    }
}
