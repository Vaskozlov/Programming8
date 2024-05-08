package server.commands

import lib.net.udp.User
import collection.CollectionInterface

fun interface ServerSideCommand {
    fun execute(
        user: User?,
        database: CollectionInterface,
        argument: Any?
    ): Result<Any?>
}
