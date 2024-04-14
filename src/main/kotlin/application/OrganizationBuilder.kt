package application

import collection.Address
import collection.Organization
import lib.BufferedReaderWithQueueOfStreams
import org.example.lib.getLocalDate

object OrganizationBuilder {
    fun constructOrganization(reader: BufferedReaderWithQueueOfStreams, prototypedFromAnother: Boolean): Organization {
        val organizationBuilder = UserInteractiveOrganizationBuilder(reader, prototypedFromAnother)

        return Organization(
            null,
            organizationBuilder.getName(),
            organizationBuilder.getCoordinates(),
            getLocalDate(),
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
