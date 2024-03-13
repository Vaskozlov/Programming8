package exceptions

class OrganizationNotFoundException(description: String = "", cause: Throwable? = null) :
    Exception(description, cause)
