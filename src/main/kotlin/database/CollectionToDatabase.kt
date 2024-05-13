package database

import collection.Organization
import collection.OrganizationType
import exceptions.IllegalArgumentsForOrganizationException
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.toJavaLocalDate

class CollectionToDatabase(private val database: Database) {
    companion object {
        const val FINISH_MODIFICATION_QUERY = """
BEGIN;
DELETE FROM ORGANIZATIONS WHERE ID = ?;
UPDATE ORGANIZATIONS SET ID = ? WHERE ID = ?;
COMMIT;
            """

        const val ADD_QUERY =
            """
WITH COORDS AS (
    INSERT INTO COORDINATES (X, Y) VALUES (?, ?)
        RETURNING ID)
   , LOCATIONS AS (
    INSERT INTO LOCATION (X, Y, Z, NAME) VALUES (?, ?, ?, ?)
        RETURNING ID)
   , ADDRESSES AS (
    INSERT INTO ADDRESS (ZIP_CODE, LOCATION_ID) SELECT ?, (SELECT ID FROM LOCATIONS)
        RETURNING ID)
INSERT
INTO ORGANIZATIONS (NAME, COORDINATES_ID, CREATION_TIME, ANNUAL_TURNOVER, FULL_NAME, EMPLOYEES_COUNT,
                    ORGANIZATION_TYPE_ID, POSTAL_ADDRESS_ID, CREATOR_ID)
VALUES (?, (SELECT ID FROM COORDS),
        ?, ?, ?,
        ?, ?, (SELECT ID FROM ADDRESSES), ?);
            """

        const val REMOVE_BY_ID_QUERY = "DELETE FROM ORGANIZATIONS WHERE ID = ?;"

        const val MODIFY_ORGANIZATION = """
BEGIN;
INSERT INTO COORDINATES (ID, X, Y)
VALUES ((SELECT organizations.coordinates_id
         from organizations
         where organizations.id = ?),
        ?,
        ?)
ON CONFLICT (ID) DO UPDATE
    SET X = excluded.X,
        y = excluded.y;

INSERT INTO LOCATION (ID, X, Y, Z, NAME)
VALUES ((SELECT address.location_id
         FROM address
                  join organizations
                       on organizations.id = ? and
                          organizations.postal_address_id = address.id),
        ?,
        ?,
        ?,
        ?)
ON CONFLICT(ID) DO UPDATE
    SET X    = excluded.X,
        Y    = excluded.Y,
        Z    = excluded.Z,
        NAME = excluded.NAME;

INSERT INTO ADDRESS (ID, ZIP_CODE, LOCATION_ID)
VALUES ((SELECT organizations.postal_address_id
         FROM organizations
         WHERE organizations.id = ?), ?, 0)
ON CONFLICT(ID)
    DO UPDATE SET ZIP_CODE = excluded.ZIP_CODE;

INSERT INTO ORGANIZATIONS (ID, NAME, COORDINATES_ID, CREATION_TIME, ANNUAL_TURNOVER, FULL_NAME, EMPLOYEES_COUNT,
                           ORGANIZATION_TYPE_ID, POSTAL_ADDRESS_ID, CREATOR_ID)
VALUES (?, ?, 0, CURRENT_DATE, ?, ?, ?, ?, 0, 0)
ON CONFLICT(ID)
    DO UPDATE
    SET NAME                 = excluded.name,
        annual_turnover      = excluded.annual_turnover,
        full_name            = excluded.full_name,
        employees_count      = excluded.employees_count,
        organization_type_id = excluded.organization_type_id;
COMMIT;
        """
    }

    fun addOrganization(organization: Organization) = runBlocking {
        val organizationType = organizationTypeToId(organization.type)
        val location = organization.postalAddress?.town
        val coordinates = organization.coordinates

        database.runCatching {
            executeUpdate(
                ADD_QUERY,
                listOf(
                    coordinates?.x,
                    coordinates?.y,
                    location?.x,
                    location?.y,
                    location?.z,
                    location?.name,
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
        }.onFailure {
            throw IllegalArgumentsForOrganizationException()
        }

        database.executeQuery(
            "SELECT ID FROM ORGANIZATIONS WHERE FULL_NAME = ? ORDER BY ID DESC LIMIT 1", listOf(
                organization.fullName
            )
        ).first().getInt("ID")
    }

    fun modifyOrganization(organization: Organization) {
        val organizationType = organizationTypeToId(organization.type)
        val location = organization.postalAddress?.town
        val coordinates = organization.coordinates

        runCatching {
            database.executeUpdate(
                MODIFY_ORGANIZATION, listOf(
                    organization.id,
                    coordinates?.x,
                    coordinates?.y,
                    organization.id,
                    location?.x,
                    location?.y,
                    location?.z,
                    location?.name,
                    organization.id,
                    organization.postalAddress?.zipCode,
                    organization.id,
                    organization.name,
                    organization.annualTurnover,
                    organization.fullName,
                    organization.employeesCount,
                    organizationType
                )
            )
        }.onFailure {
            database.executeUpdate("ROLLBACK;")
            throw IllegalArgumentsForOrganizationException()
        }
    }

    fun removeOrganizationByID(id: Int) {
        database.executeUpdate(REMOVE_BY_ID_QUERY, listOf(id))
    }

    private fun organizationTypeToId(type: OrganizationType?): Int? = runBlocking {
        database.executeQuery(
            "SELECT ID FROM ORGANIZATION_TYPES WHERE NAME = ?",
            listOf(type.toString())
        ).firstOrNull()?.getInt("ID")
    }
}