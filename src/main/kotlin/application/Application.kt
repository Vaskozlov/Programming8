package application

import database.DatabaseInterface
import exceptions.KeyboardInterruptException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import lib.BufferedReaderWithQueueOfStreams
import lib.Localization
import lib.collections.CircledStorage
import network.client.DatabaseCommand
import org.example.client.commands.*
import org.example.client.commands.core.Command
import java.io.InputStreamReader

class Application(val database: DatabaseInterface, dispatcher: CoroutineDispatcher) {
    private val applicationScope = CoroutineScope(dispatcher)
    val commandsHistory: CircledStorage<String> = CircledStorage(11)
    val bufferedReaderWithQueueOfStreams: BufferedReaderWithQueueOfStreams = BufferedReaderWithQueueOfStreams(
        InputStreamReader(System.`in`)
    )

    private var running = false
    private var localNameToDatabaseCommand: HashMap<String, DatabaseCommand> = HashMap()

    private val databaseCommandToExecutor = mapOf(
        DatabaseCommand.HELP to HelpCommand(),
        DatabaseCommand.INFO to InfoCommand(database),
        DatabaseCommand.SHOW to ShowCommand(database),
        DatabaseCommand.ADD to AddCommand(database),
        DatabaseCommand.UPDATE to UpdateCommand(database),
        DatabaseCommand.REMOVE_BY_ID to RemoveByIdCommand(database),
        DatabaseCommand.CLEAR to ClearCommand(database),
        DatabaseCommand.SAVE to SaveCommand(database),
        DatabaseCommand.READ to ReadCommand(database),
        DatabaseCommand.EXECUTE_SCRIPT to ExecuteScriptCommand(this),
        DatabaseCommand.EXIT to ExitCommand(this),
        DatabaseCommand.REMOVE_HEAD to RemoveHeadCommand(database),
        DatabaseCommand.ADD_IF_MAX to AddIfMaxCommand(database),
        DatabaseCommand.HISTORY to PrintHistoryCommand(this),
        DatabaseCommand.MAX_BY_FULL_NAME to MaxByFullNameCommand(database),
        DatabaseCommand.REMOVE_ALL_BY_POSTAL_ADDRESS to RemoveAllByPostalAddressCommand(
            database
        ),
        DatabaseCommand.SUM_OF_ANNUAL_TURNOVER to SumOfAnnualTurnoverCommand(database)
    )

    private val argumentForCommand: Map<DatabaseCommand, (String?) -> Any?> = mapOf(
        DatabaseCommand.HELP to { null },
        DatabaseCommand.INFO to { null },
        DatabaseCommand.CLEAR to { null },
        DatabaseCommand.SAVE to { null },
        DatabaseCommand.EXIT to { null },
        DatabaseCommand.REMOVE_HEAD to { null },
        DatabaseCommand.HISTORY to { null },
        DatabaseCommand.MAX_BY_FULL_NAME to { null },
        DatabaseCommand.SUM_OF_ANNUAL_TURNOVER to { null },
        DatabaseCommand.SHOW to { it },
        DatabaseCommand.READ to { it },
        DatabaseCommand.EXECUTE_SCRIPT to { it },
        DatabaseCommand.REMOVE_BY_ID to { it?.toIntOrNull() },
        DatabaseCommand.ADD to {
            OrganizationBuilder.constructOrganization(
                bufferedReaderWithQueueOfStreams,
                false
            )
        },
        DatabaseCommand.UPDATE to {
            val org = OrganizationBuilder.constructOrganization(
                bufferedReaderWithQueueOfStreams,
                true
            )
            org.id = it!!.toInt()
            org
        },
        DatabaseCommand.REMOVE_ALL_BY_POSTAL_ADDRESS to {
            OrganizationBuilder.constructAddress(
                bufferedReaderWithQueueOfStreams,
                false
            )
        },
        DatabaseCommand.ADD_IF_MAX to {
            OrganizationBuilder.constructOrganization(
                bufferedReaderWithQueueOfStreams,
                false
            )
        }
    )

    private fun loadCommands() {
        localNameToDatabaseCommand.clear()

        for ((key, value) in commandNameToDatabaseCommand) {
            localNameToDatabaseCommand[Localization.get(key)] = value
        }
    }

    private fun localize() {
        Localization.askUserForALanguage(bufferedReaderWithQueueOfStreams)
        loadCommands()
    }

    fun start() = runBlocking {
        localize()
        println(Localization.get("message.introduction"))
        running = true

        while (running) {
            try {
                val line = bufferedReaderWithQueueOfStreams.readLine()
                processCommand(line.trim())
            } catch (error: KeyboardInterruptException) {
                println(exceptionToMessage(error))
            }
        }
    }

    fun stop() {
        running = false
    }

    private suspend fun processCommand(input: String) {
        val allArguments = splitInputIntoArguments(input)

        if (allArguments.isEmpty()) {
            return
        }

        val commandName = allArguments[0]
        val commandArgument = allArguments.getOrNull(1)
        val databaseCommand = localNameToDatabaseCommand[commandName]
        val argumentExecutor = argumentForCommand[databaseCommand]

        if (databaseCommand == null || argumentExecutor == null || !argumentForCommand.containsKey(databaseCommand)) {
            System.out.printf(Localization.get("message.command.not_found"), commandName)
            return
        }

        val executionArgument = argumentExecutor.invoke(commandArgument)

        if (databaseCommand == DatabaseCommand.EXECUTE_SCRIPT) {
            executeCommand(databaseCommand, executionArgument)
        } else {
            applicationScope.launch { executeCommand(databaseCommand, executionArgument) }
        }
    }

    private suspend fun executeCommand(databaseCommand: DatabaseCommand, argument: Any?) {
        val executor = databaseCommandToExecutor[databaseCommand]
        val result = executor!!.execute(argument)

        if (result.isSuccess) {
            val successMessage = commandSuccessMessage(databaseCommand, result.getOrNull())
            println(successMessage)
        } else {
            val exception = result.exceptionOrNull()
            val errorMessage = exceptionToMessage(exception)
            println(errorMessage)
        }

        commandsHistory.add(databaseCommand.name)
    }
}
