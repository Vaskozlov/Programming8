package org.example.server.commands

import org.example.lib.net.udp.User
import database.DatabaseInterface

fun interface ServerSideCommand {
    fun execute(
        user: User?,
        database: DatabaseInterface,
        argument: Any?
    ): Result<Any?>
}
