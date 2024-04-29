import application.Application
import client.RemoteCollection
import lib.Localization
import ui.LoginPage

object Client {
    @JvmStatic
    fun main(args: Array<String>) {
        val serverIp = System.getenv("SERVER_IP") ?: "localhost"
        val serverPort = System.getenv("SERVER_PORT")?.toIntOrNull() ?: 8080
        val authFile = System.getenv("AUTH_FILE") ?: null

        val application = Application(
            authFile,
            RemoteCollection(
                serverIp,
                serverPort
            )
        )

        Thread {
            application.start()
        }.start()

        Localization.loadBundle("localization/localization", "en")
        LoginPage().apply {
            localize()
            isVisible = true
        }
    }
}