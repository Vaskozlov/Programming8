package collection

enum class NetworkCode(val value: Int) {
    SUCCESS(200),
    NOT_SUPPOERTED_COMMAND(300),
    NOT_A_MAXIMUM_ORGANIZATION(400),
    ORGANIZATION_ALREADY_EXISTS(401),
    UNABLE_TO_SAVE_TO_FILE(402),
    UNABLE_TO_READ_FROM_FILE(403),
    NOT_FOUND(404),
    ORGANIZATION_KEY_ERROR(405),
    INVALID_OUTPUT_FORMAT(406),
    UNAUTHORIZED(407),
    ACCESS_LIMITED(408),
    ILLEGAL_ARGUMENTS(409),
    FAILURE(500),
}