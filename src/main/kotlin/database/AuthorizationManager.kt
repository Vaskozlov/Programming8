package org.example.database

import org.example.database.auth.AuthorizationInfo
import org.example.database.auth.Login

class AuthorizationManager(private val database: Database) {
    suspend fun getLogins() =
        database.executeQuery("SELECT LOGIN FROM USERS").map { it.getString("LOGIN") }

    suspend fun loginExists(login: Login) = database.executeQuery(
        "SELECT 1 FROM USERS WHERE EXISTS (SELECT 1 FROM USERS WHERE LOGIN = ?)",
        listOf(login.toString())
    ).iterator().hasNext()

    suspend fun isValidUser(authorizationInfo: AuthorizationInfo) =
        database.executeQuery(
            "SELECT * FROM USERS WHERE LOGIN = ? AND PASSWORD = ?",
            listOf(
                authorizationInfo.login.toString(),
                authorizationInfo.password.hashedPassword()
            )
        ).iterator().hasNext()

    suspend fun getUserId(login: Login) = database.executeQuery(
        "SELECT ID FROM USERS WHERE LOGIN = ?",
        listOf(login.toString())
    ).firstOrNull()?.getInt("ID")

    suspend fun addUser(authorizationInfo: AuthorizationInfo): Result<Unit> {
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