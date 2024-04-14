package collection

object CSVHeader {
    private val header: Array<String> = arrayOf(
        "id",
        "name",
        "coordinates.x",
        "coordinates.y",
        "creation date",
        "annual turnover",
        "full name",
        "employees count",
        "type",
        "postalAddress:zipCode",
        "postalAddress.OrganizationDatabase.Location.x",
        "postalAddress.OrganizationDatabase.Location.y",
        "postalAddress.OrganizationDatabase.Location.z",
        "postalAddress.OrganizationDatabase.Location.name",
    )

    val headerAsString: String = java.lang.String.join(";", *header)
}
