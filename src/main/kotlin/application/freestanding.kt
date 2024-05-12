package application

import client.DatabaseCommand
import collection.LocalCollection
import collection.Organization
import exceptions.*
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.encodeToString
import lib.CliLocalization
import org.example.exceptions.UnauthorizedException
import java.io.IOException

val commandNameToDatabaseCommand = mapOf(
    "command.help" to DatabaseCommand.HELP,
    "command.info" to DatabaseCommand.INFO,
    "command.show" to DatabaseCommand.SHOW,
    "command.add" to DatabaseCommand.ADD,
    "command.update" to DatabaseCommand.UPDATE,
    "command.update_time" to DatabaseCommand.UPDATE_TIME,
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
            CliLocalization.get("message.collection.add.succeed")

        DatabaseCommand.REMOVE_BY_ID ->
            CliLocalization.get("message.organization_removed")

        DatabaseCommand.REMOVE_HEAD, DatabaseCommand.MAX_BY_FULL_NAME ->
            LocalCollection.prettyJson.encodeToString(argument as Organization?)

        DatabaseCommand.REMOVE_ALL_BY_POSTAL_ADDRESS ->
            CliLocalization.get("message.organizations_by_postal_address_removed")

        DatabaseCommand.UPDATE ->
            CliLocalization.get("message.organization_modified")

        DatabaseCommand.UPDATE_TIME ->
            (argument as LocalDateTime).toString()

        DatabaseCommand.CLEAR ->
            CliLocalization.get("message.collection_cleared")

        DatabaseCommand.EXIT ->
            CliLocalization.get("message.exit")

        DatabaseCommand.HISTORY -> argument as String

        DatabaseCommand.EXECUTE_SCRIPT ->
            CliLocalization.get("message.script_execution.started")

        DatabaseCommand.INFO, DatabaseCommand.SHOW, DatabaseCommand.HELP ->
            argument as String

        DatabaseCommand.SUM_OF_ANNUAL_TURNOVER ->
            String.format(
                "%s: %f.",
                CliLocalization.get("message.sum_of_annual_turnover"),
                argument as Double?
            )
    }

fun exceptionToMessage(exception: Throwable?): String =
    when (exception) {
        is FileReadException -> String.format(
            "%s %s.",
            CliLocalization.get("message.collection.load.failed"),
            exception.message!!
        )

        is FileWriteException -> String.format(
            "%s %s.",
            CliLocalization.get("message.collection.unable_to_save_to_file"),
            exception.message!!
        )

        is NotMaximumOrganizationException -> CliLocalization.get(
            "message.collection.add.max_check_failed"
        )

        is KeyboardInterruptException -> CliLocalization.get(
            "message.operation.canceled"
        )

        is IllegalArgumentException -> CliLocalization.get(
            "message.organization.modification_error"
        )

        is OrganizationAlreadyPresentedException -> CliLocalization.get(
            "message.organization.error.already_presented"
        )

        is OrganizationKeyException -> CliLocalization.get(
            "message.organization.error.key_error"
        )

        is IllegalAccessException -> CliLocalization.get(
            "message.illegal_access"
        )

        is IOException -> CliLocalization.get(
            "message.network.error"
        )

        is UnauthorizedException -> CliLocalization.get(
            "message.network.unauthorized"
        )

        is InvalidOutputFormatException -> CliLocalization.get(
            "message.show.unrecognizable_format"
        )

        is RecursionReadErrorException -> CliLocalization.get(
            "message.file.recursion"
        )

        else -> "${CliLocalization.get("message.command.failed")}, ${exception?.message}"
    }

fun splitInputIntoArguments(input: String): Array<String> =
    input.split(" +".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
