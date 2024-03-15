package server

import database.NetworkCode
import exceptions.*
import network.client.DatabaseCommand
import org.example.server.commands.*

var commandMap: Map<DatabaseCommand, ServerSideCommand> = mapOf(
    DatabaseCommand.ADD to AddCommand(),
    DatabaseCommand.ADD_IF_MAX to AddIfMaxCommand(),
    DatabaseCommand.SHOW to ShowCommand(),
    DatabaseCommand.CLEAR to ClearCommand(),
    DatabaseCommand.INFO to InfoCommand(),
    DatabaseCommand.MAX_BY_FULL_NAME to MaxByFullNameCommand(),
    DatabaseCommand.REMOVE_HEAD to RemoveHeadCommand(),
    DatabaseCommand.REMOVE_BY_ID to RemoveByIdCommand(),
    DatabaseCommand.SAVE to SaveCommand(),
    DatabaseCommand.READ to ReadCommand(),
    DatabaseCommand.REMOVE_ALL_BY_POSTAL_ADDRESS to RemoveAllByPostalAddressCommand(),
    DatabaseCommand.UPDATE to UpdateCommand(),
    DatabaseCommand.EXIT to ExitCommand(),
    DatabaseCommand.SUM_OF_ANNUAL_TURNOVER to SumOfAnnualTurnoverCommand()
)

fun errorToNetworkCode(error: Throwable?): NetworkCode {
    return when (error) {
        is OrganizationAlreadyPresentedException -> NetworkCode.ORGANIZATION_ALREADY_EXISTS

        is OrganizationNotFoundException -> NetworkCode.NOT_FOUND

        is NotMaximumOrganizationException -> NetworkCode.NOT_A_MAXIMUM_ORGANIZATION

        is FileReadException -> NetworkCode.UNABLE_TO_READ_FROM_FILE

        is FileWriteException -> NetworkCode.UNABLE_TO_SAVE_TO_FILE

        is OrganizationKeyException -> NetworkCode.ORGANIZATION_KEY_ERROR

        is InvalidOutputFormatException -> NetworkCode.INVALID_OUTPUT_FORMAT

        else -> NetworkCode.FAILURE
    }
}