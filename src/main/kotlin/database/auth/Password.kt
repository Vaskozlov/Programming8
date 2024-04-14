package org.example.database.auth

import com.google.common.hash.Hashing
import kotlinx.serialization.Serializable

@Serializable
class Password {
    companion object {
        private val regexForPassword = Regex("[\\w\\d]+")

        fun construct(password: String): Result<Password> {
            if (password.length < 4) {
                return Result.failure(Exception("Login is too short"))
            }

            if (!password.matches(regexForPassword)) {
                return Result.failure(Exception("Login contains invalid characters"))
            }

            return Result.success(Password(password))
        }
    }

    private val password: String

    private constructor(password: String) {
        this.password = password
    }

    override fun toString() = password

    fun hashedPassword() = Hashing.sha1().hashString(password, Charsets.UTF_8).toString()
}