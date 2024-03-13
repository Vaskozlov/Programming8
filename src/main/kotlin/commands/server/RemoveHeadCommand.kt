package commands.server

import database.Organization
import database.DatabaseInterface
import network.client.udp.User

class RemoveHeadCommand : ServerSideCommand() {
    override suspend fun executeImplementation(
        user: User?,
        database: DatabaseInterface,
        argument: Any?,
    ): Result<Organization?> {
        assert(argument == null)
        return Result.success(database.removeHead());
    }
}
