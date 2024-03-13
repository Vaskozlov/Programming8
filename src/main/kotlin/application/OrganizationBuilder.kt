package application

import database.Address
import database.Organization
import lib.BufferedReaderWithQueueOfStreams
import java.time.LocalDate

object OrganizationBuilder {
    fun constructOrganization(reader: BufferedReaderWithQueueOfStreams, prototypedFromAnother: Boolean): Organization {
        val organizationBuilder = UserInteractiveOrganizationBuilder(reader, prototypedFromAnother)

        return Organization(
            null,
            organizationBuilder.getName(),
            organizationBuilder.getCoordinates(),
            LocalDate.now(),
            organizationBuilder.getAnnualTurnover(),
            organizationBuilder.getFullName(),
            organizationBuilder.getEmployeesCount(),
            organizationBuilder.getOrganizationType(),
            organizationBuilder.getAddress()
        )
    }

    fun constructAddress(reader: BufferedReaderWithQueueOfStreams, prototypedFromAnother: Boolean): Address? {
        val organizationBuilder = UserInteractiveOrganizationBuilder(reader, prototypedFromAnother)
        return organizationBuilder.getAddress()
    }
}
