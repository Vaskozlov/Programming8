package org.example.database

import collection.Organization
import collection.OrganizationType
import kotlinx.datetime.toJavaLocalDate

class CollectionToDatabase(private val database: Database) {
    companion object {
        const val ADD_QUERY =
            """
WITH COORDS AS (
    INSERT INTO COORDINATES (X, Y) VALUES (?, ?)
        RETURNING ID)
   , LOCATIONS AS (
    INSERT INTO LOCATION (X, Y, Z, NAME) SELECT ?, ?, ?, ? WHERE ?
        RETURNING ID)
   , ADDRESSES AS (
    INSERT INTO ADDRESS (ZIP_CODE, LOCATION_ID) SELECT ?, (SELECT ID FROM LOCATIONS)
                                                WHERE EXISTS(SELECT 1 FROM LOCATIONS WHERE ID IS NOT NULL)
        RETURNING ID)
INSERT
INTO ORGANIZATIONS (NAME, COORDINATES_ID, CREATION_TIME, ANNUAL_TURNOVER, FULL_NAME, EMPLOYEES_COUNT,
                    ORGANIZATION_TYPE_ID, POSTAL_ADDRESS_ID, CREATOR_ID)
VALUES (?, (SELECT ID FROM COORDS),
        ?, ?, ?,
        ?, ?, (SELECT ID FROM ADDRESSES), ?);
            """

        const val REMOVE_BY_ID_QUERY = "DELETE FROM ORGANIZATIONS WHERE ID = ?;"

//        const val REMOVE_ALL_BY_POSTAL_ADDRESS_QUERY = """
//DELETE
//FROM ORGANIZATIONS
//WHERE POSTAL_ADDRESS_ID IN
//      (SELECT A.ID
//       FROM ADDRESS A
//                INNER JOIN LOCATION L
//                           ON A.LOCATION_ID = L.ID AND A.ZIP_CODE = ? AND L.X = ? AND
//                              L.Y = ? AND L.Z = ? AND L.NAME = ?);
//        """
    }

    suspend fun addOrganization(organization: Organization) {
        val organizationType = organizationTypeToId(organization.type)
        val location = organization.postalAddress?.town
        val coordinates = organization.coordinates

        database.executeUpdate(
            ADD_QUERY,
            listOf(
                coordinates?.x,
                coordinates?.y,
                location?.x,
                location?.y,
                location?.z,
                location?.name,
                location != null,
                organization.postalAddress?.zipCode,
                organization.name,
                organization.creationDate?.toJavaLocalDate(),
                organization.annualTurnover,
                organization.fullName,
                organization.employeesCount,
                organizationType,
                organization.creatorId
            )
        )
    }

    suspend fun modifyOrganization(organization: Organization) {

    }

    fun removeOrganizationByID(id: Int) {
        database.executeUpdate(REMOVE_BY_ID_QUERY, listOf(id))
    }

//    fun removeAllByPostalAddress(address: Address) {
//        val location = address.town
//        database.executeUpdate(
//            REMOVE_ALL_BY_POSTAL_ADDRESS_QUERY,
//            listOf(address.zipCode, location?.x, location?.y, location?.z, location?.name)
//        )
//    }

    private suspend fun organizationTypeToId(type: OrganizationType?): Int? =
        database.executeQuery(
            "SELECT ID FROM ORGANIZATION_TYPES WHERE NAME = ?",
            listOf(type.toString())
        ).firstOrNull()?.getInt("ID")
}