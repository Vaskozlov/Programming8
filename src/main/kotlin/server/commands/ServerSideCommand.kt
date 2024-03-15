package org.example.server.commands

import database.DatabaseInterface
import client.udp.User

abstract class ServerSideCommand {
    suspend fun execute(
        user: User?,
        database: DatabaseInterface,
        argument: Any? = null
    ): Result<Any?> {
        return try {
            executeImplementation(user, database, argument)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    protected abstract suspend fun executeImplementation(
        user: User?,
        database: DatabaseInterface,
        argument: Any?
    ): Result<Any?>
}
