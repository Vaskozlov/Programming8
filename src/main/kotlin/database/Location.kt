package database

import kotlinx.serialization.Serializable
import lib.CSV.CSVStreamWriter
import lib.WritableToCSVStream

/**
 * @param z    can not be null
 * @param name nullable, length can not be greater than 933
 */
@Serializable
data class Location(val x: Double?, val y: Float?, val z: Long?, val name: String?) : WritableToCSVStream {
    fun validate() {
        requireNotNull(z) { "OrganizationDatabase.Location() z must not be null" }

        require(!(name != null && name.length > 933)) { "OrganizationDatabase.Location() name is too long! It can not contain more than than 933 symbols." }
    }

    fun allNull(): Boolean = x == null && y == null && z == null && name == null

    override fun writeToStream(stream: CSVStreamWriter) {
        stream.append(x)
        stream.append(y)
        stream.append(z)
        lib.writeNullableToStream(
            stream,
            name,
            1,
            stream::append
        )
    }
}
