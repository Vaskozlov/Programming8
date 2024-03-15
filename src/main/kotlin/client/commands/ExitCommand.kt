package org.example.client.commands

import application.Application
import org.example.client.commands.core.ApplicationDependantCommand

class ExitCommand(application: Application) : ApplicationDependantCommand(application) {
    override suspend fun executeImplementation(argument: Any?): Result<Unit?> {
        assert(argument == null)

        application.stop()
        return Result.success(null)
    }
}
