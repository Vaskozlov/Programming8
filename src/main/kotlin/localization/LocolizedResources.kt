package localization

import lib.Localization

class LocalizedResources {
    companion object {
        val welcome = Localization.get("ui.welcome")
        val address = Localization.get("ui.address")
        val port = Localization.get("ui.port")
        val login = Localization.get("ui.login")
        val loginButton = Localization.get("ui.login_button")
        val password = Localization.get("ui.password")
    }
}