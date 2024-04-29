package org.example.database

import kotlinx.coroutines.runBlocking
import org.example.database.auth.AuthorizationInfo
import org.example.database.auth.Login

class AuthorizationManager(private val database: Database) {
    suspend fun getLogins() =
        database.executeQuery("SELECT LOGIN FROM USERS").map { it.getString("LOGIN") }

    fun loginExists(login: Login) = runBlocking {
        database.executeQuery(
            "SELECT COUNT(ID) > 0 FROM USERS WHERE LOGIN = ?",
            listOf(login.toString())
        ).iterator().hasNext()
    }

    fun getUserId(authorizationInfo: AuthorizationInfo): Int? = runBlocking {
        database.executeQuery(
            "SELECT ID FROM USERS WHERE LOGIN = ? AND PASSWORD = ?",
            listOf(
                authorizationInfo.login.toString(),
                authorizationInfo.password.hashedPassword()
            )
        ).firstOrNull()?.getInt("ID")
    }

    fun getUserId(login: Login) = runBlocking {
        database.executeQuery(
            "SELECT ID FROM USERS WHERE LOGIN = ?",
            listOf(login.toString())
        ).firstOrNull()?.getInt("ID")
    }

    fun addUser(authorizationInfo: AuthorizationInfo): Result<Unit> {
        if (loginExists(authorizationInfo.login)) {
            return Result.failure(Exception("User already exists"))
        }

        insertUserIntoDatabase(authorizationInfo)
        return Result.success(Unit)
    }

    private fun insertUserIntoDatabase(authorizationInfo: AuthorizationInfo) {
        database.executeUpdate(
            "INSERT INTO USERS (LOGIN, PASSWORD) VALUES (?, ?)",
            listOf(
                authorizationInfo.login.toString(),
                authorizationInfo.password.hashedPassword()
            )
        )
    }
}