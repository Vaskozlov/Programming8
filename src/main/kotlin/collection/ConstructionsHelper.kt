package collection

fun fillAddressWithMissedInformation(first: Address?, second: Address?): Address? {
    if (first == null && second == null) {
        return null
    }

    return Address(
        first?.zipCode ?: second?.zipCode,
        fillLocationWithMissedInformation(first?.town, second?.town)
    )
}

fun fillLocationWithMissedInformation(first: Location?, second: Location?): Location? {

    if (first == null && second == null) {
        return null
    }

    return Location(
        first?.x ?: second?.x,
        first?.y ?: second?.y,
        first?.z ?: second?.z,
        first?.name ?: second?.name
    )
}

fun fillCoordinatesWithMissedInformation(first: Coordinates?, second: Coordinates?): Coordinates? {
    if (first == null && second == null) {
        return null
    }

    return Coordinates(
        first?.x ?: second?.x,
        first?.y ?: second?.y
    )
}
