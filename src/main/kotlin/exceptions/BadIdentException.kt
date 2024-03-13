package exceptions

class BadIdentException(description: String = "", cause: Throwable? = null) :
    Error(description, cause)