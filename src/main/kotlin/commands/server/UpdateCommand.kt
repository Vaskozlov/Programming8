package commands.server

import database.Organization
import database.DatabaseInterface
import network.client.udp.User

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
