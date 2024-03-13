package exceptions

class CommandNotExistsException(description: String = "", cause: Throwable? = null) :
    Exception(description, cause)
