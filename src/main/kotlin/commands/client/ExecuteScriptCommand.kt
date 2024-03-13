package commands.client

import application.Application
import commands.client.core.ApplicationDependantCommand
import exceptions.FileReadException
import java.io.FileReader

class ExecuteScriptCommand(application: Application) :
    ApplicationDependantCommand(application) {
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
