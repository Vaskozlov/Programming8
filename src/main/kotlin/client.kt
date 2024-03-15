package org.example

import application.Application
import client.RemoteDatabase
import kotlinx.coroutines.Dispatchers
import server.AuthorizationInfo

object Client {
    @JvmStatic
    fun main(args: Array<String>) {
        val serverIp = System.getenv("SERVER_IP") ?: "localhost"
        val serverPort = System.getenv("SERVER_PORT")?.toIntOrNull() ?: 8080
        val authFile = System.getenv("AUTH_FILE") ?: null

        val application = Application(
            RemoteDatabase(
                AuthorizationInfo("vaskozlov", "123"),
                serverIp,
                serverPort
            ), Dispatchers.Unconfined
        )

        application.start()
    }
}