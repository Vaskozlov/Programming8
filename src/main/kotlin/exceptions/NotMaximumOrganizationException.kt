package exceptions

class NotMaximumOrganizationException(description: String = "", cause: Throwable? = null) :
    Exception(description, cause)
