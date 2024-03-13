package commands.client

import application.Application
import commands.client.core.ApplicationDependantCommand
import lib.collections.CircledStorage

class PrintHistoryCommand(application: Application) :
    ApplicationDependantCommand(application) {
    override suspend fun executeImplementation(argument: Any?): Result<CircledStorage<String>?> {
        assert(argument == null)

        return Result.success(application.commandsHistory)
    }
}
