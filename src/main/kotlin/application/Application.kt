package application

import client.DatabaseCommand
import collection.Address
import collection.CollectionInterface
import collection.Organization
import database.auth.AuthorizationInfo
import database.auth.Login
import exceptions.NotMaximumOrganizationException
import exceptions.OrganizationKeyException
import exceptions.OrganizationNotFoundException
import kotlinx.serialization.json.Json
import lib.BufferedReaderWithQueueOfStreams
import lib.CliLocalization
import lib.ExecutionStatus
import lib.IOHelper
import org.example.client.Command
import org.example.database.auth.Password
import java.io.IOException
import java.io.InputStreamReader

class Application(
    private val authFile: String?,
    private val database: CollectionInterface,
) {
    private val bufferedReaderWithQueueOfStreams: BufferedReaderWithQueueOfStreams = BufferedReaderWithQueueOfStreams(
        InputStreamReader(System.`in`)
    )

    private var running = false
    private var localNameToDatabaseCommand: HashMap<String, DatabaseCommand> = HashMap()

    private val databaseCommandToExecutor = mapOf(
        DatabaseCommand.HELP to Command
        { _, _ ->
            Result.success(CliLocalization.get("message.help"))
        },

        DatabaseCommand.INFO to Command
        { oDatabase, _ ->
            Result.success(oDatabase.getInfo())
        },

        DatabaseCommand.SHOW to Command
        { oDatabase, _ ->
            Result.success(oDatabase.toJson())
        },

        DatabaseCommand.ADD to Command
        { oDatabase, argument ->
            oDatabase.add(argument as Organization)
            Result.success(null)
        },

        DatabaseCommand.UPDATE to Command
        { oDatabase, argument ->
            oDatabase.modifyOrganization(argument as Organization)
            Result.success(null)
        },

        DatabaseCommand.UPDATE_TIME to Command
        { oDatabase, _ ->
            Result.success(oDatabase.getLastModificationTime())
        },

        DatabaseCommand.REMOVE_BY_ID to Command
        { oDatabase, argument ->
            oDatabase.removeById(argument as Int)
                .takeIf { it == ExecutionStatus.SUCCESS }
                ?.let { Result.success(null) }
                ?: Result.failure<Organization>(OrganizationKeyException("$argument"))
        },

        DatabaseCommand.CLEAR to Command
        { oDatabase, _ ->
            oDatabase.clear()
        },

        DatabaseCommand.EXECUTE_SCRIPT to Command
        { _, argument ->
            val filename = argument as String
            this.bufferedReaderWithQueueOfStreams
                .runCatching {
                    this.pushStream(filename)
                    Result.success(filename)
                }
                .onFailure { Result.failure<Command>(it) }
        },

        DatabaseCommand.EXIT to Command
        { _, _ ->
            this.stop()
            Result.success(null)
        },

        DatabaseCommand.REMOVE_HEAD to Command
        { oDatabase, _ ->
            oDatabase.removeHead()
                ?.let { Result.success(it) }
                ?: Result.failure(OrganizationNotFoundException())
        },

        DatabaseCommand.ADD_IF_MAX to Command
        { oDatabase, argument ->

            oDatabase.addIfMax(argument as Organization)
                .takeIf { it == ExecutionStatus.SUCCESS }
                ?.let { Result.success(null) }
                ?: Result.failure(NotMaximumOrganizationException())
        },

        DatabaseCommand.HISTORY to Command
        { oDatabase, _ ->
            Result.success(oDatabase.getHistory())
        },

        DatabaseCommand.MAX_BY_FULL_NAME to Command
        { oDatabase, _ ->
            oDatabase.maxByFullName()
                ?.let { Result.success(it) }
                ?: Result.failure(OrganizationNotFoundException())
        },

        DatabaseCommand.REMOVE_ALL_BY_POSTAL_ADDRESS to Command
        { oDatabase, argument ->
            oDatabase.removeAllByPostalAddress(argument as Address)
            Result.success(null)
        },

        DatabaseCommand.SUM_OF_ANNUAL_TURNOVER to Command
        { oDatabase, _ ->
            Result.success(oDatabase.getSumOfAnnualTurnover())
        }
    )

    private val argumentForCommand: Map<DatabaseCommand, (String?) -> Any?> = mapOf(
        DatabaseCommand.HELP to { null },
        DatabaseCommand.INFO to { null },
        DatabaseCommand.CLEAR to { null },
        DatabaseCommand.EXIT to { null },
        DatabaseCommand.REMOVE_HEAD to { null },
        DatabaseCommand.HISTORY to { null },
        DatabaseCommand.UPDATE_TIME to { null },
        DatabaseCommand.MAX_BY_FULL_NAME to { null },
        DatabaseCommand.SUM_OF_ANNUAL_TURNOVER to { null },
        DatabaseCommand.SHOW to { null },
        DatabaseCommand.EXECUTE_SCRIPT to { it },
        DatabaseCommand.REMOVE_BY_ID to { CliLocalization.toInt(it) },
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
            org.id = CliLocalization.toInt(it)!!
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
            localNameToDatabaseCommand[CliLocalization.get(key)] = value
        }
    }

    private fun askUserForALanguage(bufferedReaderWithQueueOfStreams: BufferedReaderWithQueueOfStreams) {
        println(
            """
                Choose language:
                0 en (default)
                1 ru
                """.trimIndent()
        )

        val line = bufferedReaderWithQueueOfStreams.readLine()

        when (line) {
            "", "0", "en" -> CliLocalization.setLanguage("en")
            "1", "ru" -> CliLocalization.setLanguage("ru")
            else -> {
                println("Invalid input. Try again.")
                askUserForALanguage(bufferedReaderWithQueueOfStreams)
            }
        }
    }

    private fun localize() {
        askUserForALanguage(bufferedReaderWithQueueOfStreams)
        loadCommands()
    }

    private fun login() {
        val authorizationInfo: AuthorizationInfo

        if (authFile != null) {
            val fileContext = IOHelper.readFile(authFile) ?: throw IOException("Unable to read from file $authFile")
            authorizationInfo = Json.decodeFromString(fileContext)
        } else {
            print(CliLocalization.get("message.ask.login"))
            val login = bufferedReaderWithQueueOfStreams.readLine()
            print(CliLocalization.get("message.ask.password"))
            val password = bufferedReaderWithQueueOfStreams.readLine()

            authorizationInfo =
                AuthorizationInfo(Login.construct(login).getOrThrow(), Password.construct(password).getOrThrow())
        }

        database.login(authorizationInfo)
    }

    fun start() {
        localize()
        login()
        println(CliLocalization.get("message.introduction"))
        running = true

        while (running) {
            try {
                val line = bufferedReaderWithQueueOfStreams.readLine()
                processCommand(line.trim())
            } catch (error: Throwable) {
                println(exceptionToMessage(error))
            }
        }
    }

    private fun stop() {
        running = false
    }

    private fun processCommand(input: String) {
        val allArguments = splitInputIntoArguments(input)

        if (allArguments.isEmpty()) {
            return
        }

        val commandName = allArguments[0]
        val commandArgument = allArguments.getOrNull(1)
        val databaseCommand = localNameToDatabaseCommand[commandName]
        val argumentExecutor = argumentForCommand[databaseCommand]

        if (databaseCommand == null || argumentExecutor == null || !argumentForCommand.containsKey(databaseCommand)) {
            System.out.printf(CliLocalization.get("message.command.not_found"), commandName)
            return
        }

        val executionArgument = argumentExecutor.invoke(commandArgument)
        executeCommand(databaseCommand, executionArgument)
    }

    private fun executeCommand(databaseCommand: DatabaseCommand, argument: Any?) {
        val executor = databaseCommandToExecutor[databaseCommand]
        val result = executor!!.execute(database, argument)

        if (result.isSuccess) {
            val successMessage = commandSuccessMessage(databaseCommand, result.getOrNull())
            println(successMessage)
        } else {
            val exception = result.exceptionOrNull()
            val errorMessage = exceptionToMessage(exception)
            println(errorMessage)
        }
    }
}
