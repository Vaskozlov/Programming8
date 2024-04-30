package exceptions

class InvalidLoginException(description: String = "", cause: Throwable? = null) :
    Exception(description, cause)
