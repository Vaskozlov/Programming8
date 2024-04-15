package application

import exceptions.*
import lib.Localization
import network.client.DatabaseCommand
import org.example.exceptions.UnauthorizedException
import java.io.IOException

val commandNameToDatabaseCommand = mapOf(
    "command.help" to DatabaseCommand.HELP,
    "command.info" to DatabaseCommand.INFO,
    "command.show" to DatabaseCommand.SHOW,
    "command.add" to DatabaseCommand.ADD,
    "command.update" to DatabaseCommand.UPDATE,
    "command.remove_by_id" to DatabaseCommand.REMOVE_BY_ID,
    "command.clear" to DatabaseCommand.CLEAR,
    "command.execute_script" to DatabaseCommand.EXECUTE_SCRIPT,
    "command.exit" to DatabaseCommand.EXIT,
    "command.remove_head" to DatabaseCommand.REMOVE_HEAD,
    "command.add_if_max" to DatabaseCommand.ADD_IF_MAX,
    "command.history" to DatabaseCommand.HISTORY,
    "command.max_by_full_name" to DatabaseCommand.MAX_BY_FULL_NAME,
    "command.remove_all_by_postal_address" to DatabaseCommand.REMOVE_ALL_BY_POSTAL_ADDRESS,
    "command.sum_of_annual_turnover" to DatabaseCommand.SUM_OF_ANNUAL_TURNOVER
)

fun commandSuccessMessage(command: DatabaseCommand, argument: Any?): String =
    when (command) {
        DatabaseCommand.ADD, DatabaseCommand.ADD_IF_MAX ->
            Localization.get("message.collection.add.succeed")

        DatabaseCommand.REMOVE_BY_ID ->
            Localization.get("message.organization_removed")

        DatabaseCommand.REMOVE_HEAD, DatabaseCommand.MAX_BY_FULL_NAME ->
            argument as String

        DatabaseCommand.REMOVE_ALL_BY_POSTAL_ADDRESS ->
            Localization.get("message.organizations_by_postal_address_removed")

        DatabaseCommand.UPDATE ->
            Localization.get("message.organization_modified")

        DatabaseCommand.CLEAR ->
            Localization.get("message.collection_cleared")

        DatabaseCommand.EXIT ->
            Localization.get("message.exit")

        DatabaseCommand.HISTORY -> argument as String

        DatabaseCommand.EXECUTE_SCRIPT ->
            Localization.get("message.script_execution.started")

        DatabaseCommand.INFO, DatabaseCommand.SHOW, DatabaseCommand.HELP ->
            argument as String

        DatabaseCommand.SUM_OF_ANNUAL_TURNOVER ->
            String.format(
                "%s: %f.",
                Localization.get("message.sum_of_annual_turnover"),
                argument as Double?
            )
    }

fun exceptionToMessage(exception: Throwable?): String =
    when (exception) {
        is FileReadException -> String.format(
            "%s %s.",
            Localization.get("message.collection.load.failed"),
            exception.message!!
        )

        is FileWriteException -> String.format(
            "%s %s.",
            Localization.get("message.collection.unable_to_save_to_file"),
            exception.message!!
        )

        is NotMaximumOrganizationException -> Localization.get(
            "message.collection.add.max_check_failed"
        )

        is KeyboardInterruptException -> Localization.get(
            "message.operation.canceled"
        )

        is IllegalArgumentException -> Localization.get(
            "message.organization.modification_error"
        )

        is OrganizationAlreadyPresentedException -> Localization.get(
            "message.organization.error.already_presented"
        )

        is OrganizationKeyException -> Localization.get(
            "message.organization.error.key_error"
        )

        is OrganizationNotFoundException -> Localization.get(
            "message.organization.error.key_error"
        )

        is IllegalAccessException -> Localization.get(
            "message.illegal_access"
        )

        is IOException -> Localization.get(
            "message.network.error"
        )

        is UnauthorizedException -> Localization.get(
            "message.network.unauthorized"
        )

        is InvalidOutputFormatException -> Localization.get(
            "message.show.unrecognizable_format"
        )

        is RecursionReadErrorException -> Localization.get(
            "message.file.recursion"
        )

        else -> "${Localization.get("message.command.failed")}, ${exception?.message}"
    }

fun splitInputIntoArguments(input: String): Array<String> =
    input.split(" +".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
