package exceptions

class OrganizationAlreadyPresentedException(description: String = "", cause: Throwable? = null) :
    Exception(description, cause)