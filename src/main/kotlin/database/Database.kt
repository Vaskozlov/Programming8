package org.example.database

import org.intellij.lang.annotations.Language
import java.sql.Connection
import java.sql.Driver
import java.sql.DriverManager
import java.sql.PreparedStatement

class Database {
    private var connection: Connection? = null

    init {
        registerDriver(org.postgresql.Driver())
    }

    fun registerDriver(driver: Driver) {
        DriverManager.registerDriver(driver)
    }

    fun connect(url: String) {
        connection = DriverManager.getConnection(url)
    }

    private fun prepareStatement(
        @Language("SQL") query: String,
        arguments: List<Any> = emptyList<Any>()
    ): PreparedStatement {
        val statement = connection!!.prepareStatement(query)

        for (i in arguments.indices) {
            statement.setObject(i + 1, arguments[i])
        }

        return statement
    }

    suspend fun executeQuery(@Language("SQL") query: String, arguments: List<Any> = emptyList<Any>()) = sequence {
        connection?.let {
            prepareStatement(query, arguments).use {
                val result = it.executeQuery()
                while (result.next()) {
                    yield(result)
                }
            }
        }
    }

    fun executeUpdate(@Language("SQL") query: String, arguments: List<Any> = emptyList<Any>()) {
        connection?.let {
            prepareStatement(query, arguments).use {
                it.executeUpdate()
            }
        }
    }
}