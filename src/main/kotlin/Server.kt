package org.example

import lib.Localization
import org.example.database.Database
import server.CollectionCommandsReceiver

object Server {
    @JvmStatic
    fun main(args: Array<String>) {
        val port = System.getenv("SERVER_PORT")?.toIntOrNull() ?: 8080

        Localization.loadBundle("localization/localization", "en")
        val database = Database()
        database.connect("jdbc:postgresql://localhost:5432/programming7")

        CollectionCommandsReceiver(
            port,
            database
        ).use { it.run() }
    }
}