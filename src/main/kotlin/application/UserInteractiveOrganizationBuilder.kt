package application

import collection.Address
import collection.Coordinates
import collection.Location
import collection.OrganizationType
import exceptions.KeyboardInterruptException
import lib.BufferedReaderWithQueueOfStreams
import lib.CliLocalization

/**
 * Reads information needed to construct Organization from the buffer
 */
class UserInteractiveOrganizationBuilder(
    private val reader: BufferedReaderWithQueueOfStreams,
    private val allowBlank: Boolean
) {
    fun getName(): String? =
        getString(
            CliLocalization.get("organization_builder.input.organization_name"),
            false
        )

    fun getCoordinates(): Coordinates {
        val x = getNumber(
            CliLocalization.get("organization_builder.input.coordinate.x"),
            false,
            CliLocalization::toLong
        )

        val y = getNumber(
            CliLocalization.get("organization_builder.input.coordinate.y"),
            false,
            CliLocalization::toLong
        )

        if (y != null && y > 464) {
            println(CliLocalization.get("organization_builder.input.coordinate.y.limit.message"))
            return getCoordinates()
        }

        return Coordinates(x, y)
    }

    fun getAnnualTurnover(): Double? {
        val result = getNumber(
            CliLocalization.get("organization_builder.input.annual_turnover"),
            false,
            CliLocalization::toDouble
        )

        if (result != null && result <= 0) {
            println(CliLocalization.get("organization_builder.input.annual_turnover.limit.message"))
            return getAnnualTurnover()
        }

        return result
    }

    fun getFullName(): String? = getString(
        CliLocalization.get("organization_builder.input.full_name"),
        false
    )

    fun getEmployeesCount(): Int? =
        getNumber(
            CliLocalization.get("organization_builder.input.employees_count"),
            true,
            CliLocalization::toInt
        )

    fun getOrganizationType(): OrganizationType? {
        System.out.printf(CliLocalization.get("organization_builder.input.type"))
        val line = reader.readLine()
        checkForExitCommand(line)

        return when (line) {
            "null", "" -> null
            "0" -> OrganizationType.COMMERCIAL
            "1" -> OrganizationType.PUBLIC
            "2" -> OrganizationType.PRIVATE_LIMITED_COMPANY
            "3" -> OrganizationType.OPEN_JOINT_STOCK_COMPANY
            else -> {
                println(CliLocalization.get("organization_builder.input.type.invalid_input"))
                return getOrganizationType()
            }
        }
    }

    fun getAddress(): Address? {
        val zipCode = getString(
            CliLocalization.get("organization_builder.input.zip_code"),
            true
        )

        if (zipCode != null && zipCode.length < 3) {
            println(CliLocalization.get("organization_builder.input.zip_code.limit.message"))
            return getAddress()
        }

        if (zipCode == null) {
            val answer = getString(
                CliLocalization.get("organization_builder.input.address.possible_null"),
                true
            )

            if (answer == null) {
                return null
            }
        }

        val x = getNumber(
            CliLocalization.get("organization_builder.input.location.x"),
            false,
            CliLocalization::toDouble
        )

        val y = getNumber(
            CliLocalization.get("organization_builder.input.location.y"),
            false,
            CliLocalization::toFloat
        )

        val z = getNumber(
            CliLocalization.get("organization_builder.input.location.z"),
            false,
            CliLocalization::toLong
        )

        val name = getString(
            CliLocalization.get("organization_builder.input.location.name"),
            true
        )

        return Address(zipCode, Location(x, y, z, name))
    }

    private fun getString(
        fieldName: String,
        nullable: Boolean
    ): String? {
        val line = getInput(fieldName, nullable)

        if (line.contains(";")) {
            println(CliLocalization.get("message.input.error.semicolon"))
            return getString(fieldName, nullable)
        }

        if (needToTakeDataFromProvidedOrganization(line) && allowBlank) {
            return null
        }

        checkForExitCommand(line)

        if (isNullInput(nullable, line)) {
            return null
        }

        return line
    }

    private fun <T> getNumber(
        fieldName: String,
        nullable: Boolean,
        function: (String) -> T
    ): T? {
        val line = getInput(fieldName, nullable)

        if (needToTakeDataFromProvidedOrganization(line) && allowBlank) {
            return null
        }

        checkForExitCommand(line)

        if (isNullInput(nullable, line)) {
            return null
        }

        return function.invoke(line) ?: getNumber(fieldName, nullable, function)
    }

    private fun getInput(
        fieldName: String,
        nullable: Boolean
    ): String {
        System.out.printf(
            CliLocalization.get("organization_builder.input.get"),
            fieldName,
            if (nullable) String.format(" (%s) ", CliLocalization.get("input.nullable")) else ""
        )

        return reader.readLine()
    }

    private fun checkForExitCommand(line: String?) {
        if (line == "exit") {
            throw KeyboardInterruptException()
        }
    }

    private fun isNullInput(nullable: Boolean, input: String): Boolean {
        return nullable && (input.isEmpty() || input == CliLocalization.get("input.null"))
    }

    private fun needToTakeDataFromProvidedOrganization(line: String): Boolean {
        return line.isEmpty()
    }
}
