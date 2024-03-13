package exceptions

class RecursionReadErrorException(description: String = "", cause: Throwable? = null) :
    Exception(description, cause)