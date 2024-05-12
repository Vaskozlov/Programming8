package exceptions

class InvalidOutputFormatException(description: String = "", cause: Throwable? = null) :
    Exception(description, cause)