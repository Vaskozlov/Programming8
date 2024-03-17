package org.example.server.commands

import client.udp.User
import database.DatabaseInterface

fun interface ServerSideCommand {
    suspend fun execute(
        user: User?,
        database: DatabaseInterface,
        argument: Any?
    ): Any
}
