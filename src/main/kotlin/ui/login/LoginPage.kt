package ui.login

import client.RemoteCollection
import database.auth.AuthorizationInfo
import database.auth.Login
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.database.auth.Password
import ui.lib.GuiLocalization
import ui.lib.MigFontLayout
import ui.lib.buttonClickAdapter
import ui.lib.calculateFontSize
import ui.panels.SelectLanguagePanel
import ui.table.TablePage
import java.util.concurrent.TimeoutException
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JTextField

class LoginPage : JFrame() {
    companion object {
        val portRegex = Regex("^[0-9]{1,5}\$")
        const val FIELDS_LENGTH = 30
    }

    private val loginScope = CoroutineScope(Dispatchers.Default)
    private val addressLabel = JLabel()
    private val portLabel = JLabel()
    private val loginLabel = JLabel()
    private val passwordLabel = JLabel()
    private val welcomeLabel = JLabel()

    private val selectLangaugePanel = SelectLanguagePanel(24, loginScope)

    private val addressEditor = JTextField(FIELDS_LENGTH)
    private val portEditor = JTextField(FIELDS_LENGTH)
    private val loginEditor = JTextField(FIELDS_LENGTH)
    private val passwordEditor = JTextField(FIELDS_LENGTH)
    private val loginButton = buttonClickAdapter { loginPressed() }

    private val layout = MigFontLayout(
        "",
        "[fill,grow,40%][fill,grow,50%]",
        "[fill,grow]"
    ) {
        fontSize = calculateFontSize(24)
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

        val collection = RemoteCollection(addressEditor.text, GuiLocalization.toInt(portEditor.text)!!)

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
                    JOptionPane.showMessageDialog(
                        this,
                        GuiLocalization.get("ui.unable_to_connect")
                    )

                else ->
                    JOptionPane.showMessageDialog(
                        this,
                        GuiLocalization.get("ui.unable_to_login")
                    )
            }

            return
        }.onSuccess {
            isVisible = false

            GuiLocalization.clearElements()
            TablePage(collection, loginEditor.text).apply {
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

        add(welcomeLabel, "span 2,wrap")

        add(addressLabel)
        add(addressEditor, "wrap")

        add(portLabel)
        add(portEditor, "wrap")

        add(loginLabel)
        add(loginEditor, "wrap")

        add(passwordLabel)
        add(passwordEditor, "wrap")

        add(selectLangaugePanel, "span 2,wrap")

        add(loginButton)

        GuiLocalization.addElement(
            "ui.welcome", welcomeLabel
        )
        GuiLocalization.addElement("ui.address", addressLabel)
        GuiLocalization.addElement("ui.port", portLabel)
        GuiLocalization.addElement("ui.login", loginLabel)
        GuiLocalization.addElement("ui.password", passwordLabel)
        GuiLocalization.addElement("ui.login_button", loginButton)

        loginScope.launch {
            GuiLocalization.updateUiElements()
        }
    }
}