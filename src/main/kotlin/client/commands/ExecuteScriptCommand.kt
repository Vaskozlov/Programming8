package org.example.client.commands

import application.Application
import org.example.client.commands.core.ApplicationDependantCommand

class ExecuteScriptCommand(application: Application) : ApplicationDependantCommand(application) {
    override suspend fun executeImplementation(argument: Any?): Result<String> {
        val filename = argument as String

        return try {
            application.bufferedReaderWithQueueOfStreams.pushStream(filename)
            Result.success(filename)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
