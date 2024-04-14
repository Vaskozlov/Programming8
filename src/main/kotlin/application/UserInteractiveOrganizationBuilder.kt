package application

import collection.Address
import collection.Coordinates
import collection.Location
import collection.OrganizationType
import exceptions.KeyboardInterruptException
import lib.BufferedReaderWithQueueOfStreams
import lib.Localization

/**
 * Reads information needed to construct Organization from the buffer
 */
class UserInteractiveOrganizationBuilder(
    private val reader: BufferedReaderWithQueueOfStreams,
    private val allowBlank: Boolean
) {
    fun getName(): String? =
        getString(
            Localization.get("organization_builder.input.organization_name"),
            false
        )

    fun getCoordinates(): Coordinates {
        val x = getNumber(
            Localization.get("organization_builder.input.coordinate.x"),
            false,
            String::toLongOrNull
        )

        val y = getNumber(
            Localization.get("organization_builder.input.coordinate.y"),
            false,
            String::toLongOrNull
        )

        if (y != null && y > 464) {
            println(Localization.get("organization_builder.input.coordinate.y.limit.message"))
            return getCoordinates()
        }

        return Coordinates(x, y)
    }

    fun getAnnualTurnover(): Double? {
        val result = getNumber(
            Localization.get("organization_builder.input.annual_turnover"),
            false,
            String::toDoubleOrNull
        )

        if (result != null && result <= 0) {
            println(Localization.get("organization_builder.input.annual_turnover.limit.message"))
            return getAnnualTurnover()
        }

        return result
    }

    fun getFullName(): String? = getString(
        Localization.get("organization_builder.input.full_name"),
        false
    )

    fun getEmployeesCount(): Int? =
        getNumber(
            Localization.get("organization_builder.input.employees_count"),
            true,
            String::toIntOrNull
        )

    fun getOrganizationType(): OrganizationType? {
        System.out.printf(Localization.get("organization_builder.input.type"))
        val line = reader.readLine()
        checkForExitCommand(line)

        return when (line) {
            "null", "" -> null
            "0" -> OrganizationType.COMMERCIAL
            "1" -> OrganizationType.PUBLIC
            "2" -> OrganizationType.PRIVATE_LIMITED_COMPANY
            "3" -> OrganizationType.OPEN_JOINT_STOCK_COMPANY
            else -> {
                println(Localization.get("organization_builder.input.type.invalid_input"))
                return getOrganizationType()
            }
        }
    }

    fun getAddress(): Address? {
        val zipCode = getString(
            Localization.get("organization_builder.input.zip_code"),
            true
        )

        if (zipCode != null && zipCode.length < 3) {
            println(Localization.get("organization_builder.input.zip_code.limit.message"))
            return getAddress()
        }

        if (zipCode == null) {
            val answer = getString(
                Localization.get("organization_builder.input.address.possible_null"),
                true
            )

            if (answer == null) {
                return null
            }
        }

        val x = getNumber(
            Localization.get("organization_builder.input.location.x"),
            false,
            String::toDoubleOrNull
        )

        val y = getNumber(
            Localization.get("organization_builder.input.location.y"),
            false,
            String::toFloatOrNull
        )

        val z = getNumber(
            Localization.get("organization_builder.input.location.z"),
            false,
            String::toLong
        )

        val name = getString(
            Localization.get("organization_builder.input.location.name"),
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
            println(Localization.get("message.input.error.semicolon"))
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
            Localization.get("organization_builder.input.get"),
            fieldName,
            if (nullable) String.format(" (%s) ", Localization.get("input.nullable")) else ""
        )

        return reader.readLine()
    }

    private fun checkForExitCommand(line: String?) {
        if (line == "exit") {
            throw KeyboardInterruptException()
        }
    }

    private fun isNullInput(nullable: Boolean, input: String): Boolean {
        return nullable && (input.isEmpty() || input == Localization.get("input.null"))
    }

    private fun needToTakeDataFromProvidedOrganization(line: String): Boolean {
        return line.isEmpty()
    }
}
