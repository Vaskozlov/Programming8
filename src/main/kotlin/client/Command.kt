package org.example.client

import collection.CollectionInterface

fun interface Command {
    fun executeImplementation(database: CollectionInterface, argument: Any?): Result<Any?>

    fun execute(database: CollectionInterface, argument: Any? = null): Result<Any?> {
        return try {
            executeImplementation(database, argument)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }
}
