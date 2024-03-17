package exceptions

class BadIdentException(description: String = "", cause: Throwable? = null) :
    Exception(description, cause)