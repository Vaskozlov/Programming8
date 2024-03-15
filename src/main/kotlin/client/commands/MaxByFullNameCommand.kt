package org.example.client.commands

import database.DatabaseInterface
import database.Organization
import exceptions.OrganizationNotFoundException
import org.example.client.commands.core.DatabaseDependantCommand

class MaxByFullNameCommand(organizationDatabase: DatabaseInterface) :
    DatabaseDependantCommand(organizationDatabase) {
    override suspend fun executeImplementation(argument: Any?): Result<Organization> {
        val maxOrganization = database.maxByFullName()

        if (maxOrganization == null) {
            return Result.failure(OrganizationNotFoundException())
        }

        return Result.success(maxOrganization)
    }
}
