package org.example.server.commands

import database.Organization
import database.DatabaseInterface
import exceptions.OrganizationNotFoundException
import client.udp.User

class MaxByFullNameCommand : ServerSideCommand() {
    override suspend fun executeImplementation(
        user: User?,
        database: DatabaseInterface,
        argument: Any?
    ): Result<Organization?> {
        assert(argument == null)
        val maxOrganization = database.maxByFullName()

        if (maxOrganization == null) {
            return Result.failure(OrganizationNotFoundException())
        }

        return Result.success(maxOrganization)
    }
}
