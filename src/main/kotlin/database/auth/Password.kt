package org.example.database.auth

import com.google.common.hash.Hashing
import kotlinx.serialization.Serializable

@Serializable
class Password {
    companion object {
        private val regexForPassword = Regex("\\w{0,128}")

        fun construct(password: String): Result<Password> {
            if (password.length < 4) {
                return Result.failure(Exception("Password is too short"))
            }

            if (password.length > 120) {
                return Result.failure(Exception("Password is too long"))
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

    // Salt can be added to the end and the beginning of the password in order to increase security
    fun hashedPassword() = Hashing.sha1().hashString(password, Charsets.UTF_8).toString()
}