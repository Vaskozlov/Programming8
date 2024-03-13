package commands.client

import commands.client.core.DatabaseDependantCommand
import database.Address
import database.DatabaseInterface

class RemoveAllByPostalAddressCommand(
    organizationDatabase: DatabaseInterface
) : DatabaseDependantCommand(organizationDatabase) {
    override suspend fun executeImplementation(argument: Any?): Result<Unit?> {
        database.removeAllByPostalAddress(argument as Address)
        return Result.success(null)
    }
}
