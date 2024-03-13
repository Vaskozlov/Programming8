package exceptions

class OrganizationKeyException(description: String = "", cause: Throwable? = null) :
    Exception(description, cause)
