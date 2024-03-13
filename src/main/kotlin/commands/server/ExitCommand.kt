package commands.server

import database.DatabaseInterface
import network.client.udp.User

class ExitCommand : ServerSideCommand() {
    override suspend fun executeImplementation(
        user: User?,
        database: DatabaseInterface,
        argument: Any?
    ): Result<Unit?> {
        return Result.success(null)
    }
}
