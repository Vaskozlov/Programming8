package exceptions

class KeyboardInterruptException(description: String = "", cause: Throwable? = null) :
    Exception(description, cause) 