package org.example.database.auth

import exceptions.InvalidLoginException
import kotlinx.serialization.Serializable

@Serializable
class Login {
    companion object {
        private val regexForLogin = Regex("\\w{0,128}")

        fun construct(login: String): Result<Login> {
            if (login.length < 4) {
                return Result.failure(InvalidLoginException("Login is too short"))
            }

            if (login.length > 120) {
                return Result.failure(InvalidLoginException("Login is too long"))
            }

            if (!login.matches(regexForLogin)) {
                return Result.failure(InvalidLoginException("Login contains invalid characters"))
            }

            return Result.success(Login(login))
        }
    }

    private val login: String

    private constructor(login: String) {
        this.login = login
        Thread()
    }

    override fun toString() = login
}