package org.example.client.commands

import database.DatabaseInterface
import database.Organization
import exceptions.OrganizationNotFoundException
import org.example.client.commands.core.DatabaseDependantCommand

class RemoveHeadCommand(organizationDatabase: DatabaseInterface) :
    DatabaseDependantCommand(organizationDatabase) {
    override suspend fun executeImplementation(argument: Any?): Result<Organization> {
        assert(argument == null)

        val removedOrganization = database.removeHead() ?: return Result.failure(OrganizationNotFoundException())
        return Result.success(removedOrganization)
    }
}
