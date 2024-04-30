package exceptions

class InvalidPasswordException(description: String = "", cause: Throwable? = null) :
    Exception(description, cause)
