package server.commands

import collection.CollectionInterface
import lib.net.udp.User

fun interface ServerSideCommand {
    fun execute(
        user: User?,
        database: CollectionInterface,
        argument: Any?
    ): Result<Any?>
}
