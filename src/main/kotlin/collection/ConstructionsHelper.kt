package collection

fun fillAddressWithMissedInformation(first: Address?, second: Address?): Address? {
    if (first == null && second == null) {
        return null
    }

    return Address(
        first?.zipCode ?: second?.zipCode,
        first?.town ?: second?.town
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
