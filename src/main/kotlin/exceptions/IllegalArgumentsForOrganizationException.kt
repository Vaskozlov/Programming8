package exceptions

class IllegalArgumentsForOrganizationException(description: String = "", cause: Throwable? = null) :
    IllegalArgumentException(description, cause) 