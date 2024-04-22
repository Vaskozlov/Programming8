package ui

import client.RemoteCollection
import localization.LocalizedResources
import org.example.database.auth.AuthorizationInfo
import org.example.database.auth.Login
import org.example.database.auth.Password
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.ActionEvent
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

    private val gridBag = GridBagLayout()
    private val gridBagConstraint = GridBagConstraints()

    fun localize() {
        addressLabel.text = LocalizedResources.address
        portLabel.text = LocalizedResources.port
        loginLabel.text = LocalizedResources.login
        passwordLabel.text = LocalizedResources.password
        welcomeLabel.text = LocalizedResources.welcome
        loginButton.text = LocalizedResources.login
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
            JOptionPane.showMessageDialog(this, "Unable to login.")
            return
        }

        TablePage(collection).apply {
            isVisible = true
        }
    }

    private fun checkAddress() = addressEditor.text.isNotEmpty()
    private fun checkPort() = portEditor.text.matches(portRegex)

    private fun checkLogin() = Login.construct(loginEditor.text).isSuccess
    private fun checkPassword() = Password.construct(passwordEditor.text).isSuccess

    private fun makeComponent(
        component: Component,
        c: GridBagConstraints,
    ) {
        gridBag.setConstraints(component, c)
        add(component)
    }

    init {
        addressEditor.text = "localhost"
        portEditor.text = "8080"
        loginEditor.text = "vaskozlov"
        passwordEditor.text = "1234"

        defaultCloseOperation = EXIT_ON_CLOSE
        contentPane.layout = gridBag

        gridBagConstraint.fill = GridBagConstraints.RELATIVE
        gridBagConstraint.weightx = 1.0
        gridBagConstraint.gridwidth = GridBagConstraints.REMAINDER

        makeComponent(welcomeLabel, gridBagConstraint)

        gridBagConstraint.weightx = 1.0
        gridBagConstraint.gridwidth = GridBagConstraints.RELATIVE

        makeComponent(addressLabel, gridBagConstraint)
        gridBagConstraint.gridwidth = GridBagConstraints.REMAINDER
        makeComponent(addressEditor, gridBagConstraint)

        gridBagConstraint.weightx = 1.0
        gridBagConstraint.gridwidth = GridBagConstraints.RELATIVE

        makeComponent(portLabel, gridBagConstraint)
        gridBagConstraint.gridwidth = GridBagConstraints.REMAINDER
        makeComponent(portEditor, gridBagConstraint)

        gridBagConstraint.weightx = 1.0
        gridBagConstraint.gridwidth = GridBagConstraints.RELATIVE

        makeComponent(loginLabel, gridBagConstraint)
        gridBagConstraint.gridwidth = GridBagConstraints.REMAINDER
        makeComponent(loginEditor, gridBagConstraint)

        gridBagConstraint.weightx = 0.0
        gridBagConstraint.gridwidth = GridBagConstraints.RELATIVE

        makeComponent(passwordLabel, gridBagConstraint)
        gridBagConstraint.gridwidth = GridBagConstraints.REMAINDER
        makeComponent(passwordEditor, gridBagConstraint)

        gridBagConstraint.gridwidth = GridBagConstraints.RELATIVE
        gridBagConstraint.anchor = GridBagConstraints.CENTER
        gridBagConstraint.gridx = 0
        makeComponent(loginButton, gridBagConstraint)

        pack()
    }
}