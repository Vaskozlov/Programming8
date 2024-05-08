package collection

import kotlinx.serialization.Serializable

/**
 * @param z    cannot be null
 * @param name nullable, length cannot be greater than 933
 */
@Serializable
data class Location(val x: Double?, val y: Float?, val z: Long?, var name: String?) {
    fun validate() {
        requireNotNull(z) { "OrganizationDatabase.Location() z must not be null" }

        require(!(name != null && name!!.length > 933)) { "OrganizationDatabase.Location() name is too long! It can not contain more than than 933 symbols." }
    }

    fun allNull(): Boolean = x == null && y == null && z == null && name == null
}
