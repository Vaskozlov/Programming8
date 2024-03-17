package org.example.server.commands

import client.udp.User
import database.DatabaseInterface

fun interface ServerSideCommand {
    suspend fun executeImplementation(
        user: User?,
        database: DatabaseInterface,
        argument: Any?
    ): Result<Any?>

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
}
