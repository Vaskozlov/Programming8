package commands.client.core

import database.DatabaseInterface

abstract class DatabaseDependantCommand protected constructor(
    protected var database: DatabaseInterface
) : Command()
