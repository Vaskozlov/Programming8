import application.Application
import client.RemoteCollection
import ui.login.LoginPage
import java.util.*
import javax.swing.SwingUtilities

object Client {
    @JvmStatic
    fun main(args: Array<String>) {
        val serverIp = System.getenv("SERVER_IP") ?: "localhost"
        val serverPort = System.getenv("SERVER_PORT")?.toIntOrNull() ?: 8080
        val authFile = System.getenv("AUTH_FILE") ?: null

        Locale.setDefault(Locale.of("ru"))

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

        SwingUtilities.invokeLater {
            LoginPage().apply {
                isVisible = true
            }
        }
    }
}