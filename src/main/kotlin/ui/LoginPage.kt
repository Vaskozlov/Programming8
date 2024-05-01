package ui

import client.RemoteCollection
import lib.Localization
import localization.LocalizedResources
import net.miginfocom.swing.MigLayout
import database.auth.AuthorizationInfo
import database.auth.Login
import org.example.database.auth.Password
import java.awt.event.ActionEvent
import java.util.concurrent.TimeoutException
import javax.swing.*

class LoginPage : JFrame() {
    companion object {
        val portRegex = Regex("^[0-9]{1,5}\$")
    }

    private val addressLabel = JLabel()
    private val portLabel = JLabel()
    private val loginLabel = JLabel()
    private val passwordLabel = JLabel()
    private val welcomeLabel = JLabel()

    private val addressEditor = JTextField(30)
    private val portEditor = JTextField(30)
    private val loginEditor = JTextField(30)
    private val passwordEditor = JTextField(30)
    private val buttonAction = object : AbstractAction() {
        override fun actionPerformed(e: ActionEvent) {
            loginPressed()
        }
    }

    private val loginButton = JButton(buttonAction)

    private val layout = MigLayout("",
        "[fill,40%][fill,50%]",
        "[fill,grow]")

    fun localize() {
        addressLabel.text = LocalizedResources.address
        portLabel.text = LocalizedResources.port
        loginLabel.text = LocalizedResources.login
        passwordLabel.text = LocalizedResources.password
        welcomeLabel.text = LocalizedResources.welcome
        loginButton.text = LocalizedResources.loginButton
    }

    private fun loginPressed() {
        if (!checkAddress()) {
            JOptionPane.showMessageDialog(this, "Address is empty.")
            return
        }

        if (!checkPort()) {
            JOptionPane.showMessageDialog(this, "Port is incorrect.")
            return
        }

        if (!checkLogin()) {
            JOptionPane.showMessageDialog(this, "Login is incorrect.")
            return
        }

        if (!checkPassword()) {
            JOptionPane.showMessageDialog(this, "Password is incorrect.")
            return
        }

        val collection = RemoteCollection(addressEditor.text, portEditor.text.toInt())

        // TODO: add check
        collection.login(
            AuthorizationInfo(
                Login.construct(loginEditor.text).getOrThrow(),
                Password.construct(passwordEditor.text).getOrThrow()
            )
        )

        collection.runCatching {
            collection.getLastModificationTime()
        }.onFailure {
            when (it) {
                is TimeoutException ->
                    JOptionPane.showMessageDialog(this, Localization.get("ui.unable_to_connect"))

                else ->
                    JOptionPane.showMessageDialog(this, Localization.get("ui.unable_to_login"))
            }

            return
        }.onSuccess {
            isVisible = false

            TablePage(collection).apply {
                isVisible = true
            }
        }
    }

    private fun checkAddress() = addressEditor.text.isNotEmpty()
    private fun checkPort() = portEditor.text.matches(portRegex)

    private fun checkLogin() = Login.construct(loginEditor.text).isSuccess
    private fun checkPassword() = Password.construct(passwordEditor.text).isSuccess

    init {
        addressEditor.text = "localhost"
        portEditor.text = "8080"
        loginEditor.text = "vaskozlov"
        passwordEditor.text = "1234"
        setSize(600, 400)

        defaultCloseOperation = EXIT_ON_CLOSE
        contentPane.layout = layout

        add(welcomeLabel, "wrap,dock center")

        add(addressLabel, "")
        add(addressEditor, "wrap")

        add(portLabel)
        add(portEditor, "wrap")

        add(loginLabel)
        add(loginEditor, "wrap")

        add(passwordLabel)
        add(passwordEditor, "wrap")

        add(loginButton, "dock center")
    }
}