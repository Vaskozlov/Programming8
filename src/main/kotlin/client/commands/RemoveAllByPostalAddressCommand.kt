package org.example.client.commands

import database.Address
import database.DatabaseInterface
import org.example.client.commands.core.DatabaseDependantCommand

class RemoveAllByPostalAddressCommand(organizationDatabase: DatabaseInterface) :
    DatabaseDependantCommand(organizationDatabase) {
    override suspend fun executeImplementation(argument: Any?): Result<Unit?> {
        database.removeAllByPostalAddress(argument as Address)
        return Result.success(null)
    }
}
