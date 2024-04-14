package org.example.database.auth

import kotlinx.serialization.Serializable

@Serializable
class Login {
    companion object {
        private val regexForLogin = Regex("[\\w\\d]+")

        fun construct(login: String): Result<Login> {
            if (login.length < 4) {
                return Result.failure(Exception("Login is too short"))
            }

            if (!login.matches(regexForLogin)) {
                return Result.failure(Exception("Login contains invalid characters"))
            }

            return Result.success(Login(login))
        }
    }

    private val login: String

    private constructor(login: String) {
        this.login = login
    }

    override fun toString() = login
}