package commands.server

import database.DatabaseInterface
import network.client.udp.User

class ClearCommand : ServerSideCommand() {
    override suspend fun executeImplementation(
        user: User?,
        database: DatabaseInterface,
        argument: Any?
    ): Result<Unit?> {
        assert(argument == null)
        database.clear()
        return Result.success(null)
    }
}
