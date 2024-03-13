package commands.client

import application.Application
import commands.client.core.ApplicationDependantCommand

class ExitCommand(application: Application) :
    ApplicationDependantCommand(application) {
    override suspend fun executeImplementation(argument: Any?): Result<Unit?> {
        assert(argument == null)

        application.stop()
        return Result.success(null)
    }
}
