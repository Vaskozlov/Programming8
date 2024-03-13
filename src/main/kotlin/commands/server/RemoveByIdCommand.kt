package commands.server

import database.DatabaseInterface
import network.client.udp.User

class RemoveByIdCommand : ServerSideCommand() {
    override suspend fun executeImplementation(
        user: User?,
        database: DatabaseInterface,
        argument: Any?
    ): Result<Unit?> {
        database.removeById(argument as Int)
        return Result.success(null)
    }
}
