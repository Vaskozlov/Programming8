package collection

import kotlinx.serialization.Serializable

/**
 * @param zipCode nullable must contain at least 3 character
 * @param town    cannot be null
 */
@Serializable
data class Address(val zipCode: String?, var town: Location?) {
    fun validate() {
        require(!(zipCode != null && zipCode.length < 3)) { "Invalid zip code" }
        requireNotNull(town) { "OrganizationDatabase.Address town must not be null" }
        town!!.validate()
    }

    fun simplify(): Address? {
        if (zipCode == null && (town == null || town!!.allNull())) {
            return null
        }

        return this
    }
}