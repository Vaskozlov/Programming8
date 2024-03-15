package org.example.client.commands

import lib.Localization
import org.example.client.commands.core.Command

class HelpCommand : Command() {
    override suspend fun executeImplementation(argument: Any?): Result<String> {
        assert(argument == null)

        return Result.success(Localization.get("message.help"))
    }
}
