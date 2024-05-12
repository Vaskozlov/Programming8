import database.Database
import lib.CliLocalization
import server.CollectionCommandsReceiver

object Server {
    @JvmStatic
    fun main(args: Array<String>) {
        val port = System.getenv("SERVER_PORT")?.toIntOrNull() ?: 8080
        val serverURL = System.getenv("SERVER_URL") ?: "jdbc:postgresql://localhost:5432/studs"
        val serverLogin = System.getenv("SERVER_LOGIN") ?: null
        val serverPassword = System.getenv("SERVER_PASSWORD") ?: null
        val database = Database()

        CliLocalization.setLanguage("en")
        database.connect(serverURL, serverLogin, serverPassword)

        CollectionCommandsReceiver(
            port,
            database
        ).run()
    }
}