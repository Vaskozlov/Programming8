package org.example.client.commands

import application.Application
import lib.collections.CircledStorage
import org.example.client.commands.core.ApplicationDependantCommand

class PrintHistoryCommand(application: Application) : ApplicationDependantCommand(application) {
    override suspend fun executeImplementation(argument: Any?): Result<CircledStorage<String>?> {
        assert(argument == null)

        return Result.success(application.commandsHistory)
    }
}
