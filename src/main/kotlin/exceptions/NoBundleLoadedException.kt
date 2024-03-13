package exceptions

class NoBundleLoadedException(description: String = "", cause: Throwable? = null) :
    Error(description, cause)