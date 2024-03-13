package org.example

import kotlinx.coroutines.Dispatchers
import server.DatabaseCommandsReceiver
import kotlin.io.path.Path

object Server {
    @JvmStatic
    fun main(args: Array<String>) {
        val port = System.getenv("SERVER_PORT")?.toIntOrNull() ?: 8080
        val clientsPath = System.getenv("CLIENTS_PATH")
        val databasePath = System.getenv("DATABASE_PATH")

        val receiver = DatabaseCommandsReceiver(
            port,
            Dispatchers.Unconfined, // Dispatchers.Default can be used in order to enable multithreading
            Path(clientsPath),
            Path(databasePath)
        )

        receiver.run()
    }
}