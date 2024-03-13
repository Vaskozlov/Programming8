package exceptions

class FileReadException(description: String = "", cause: Throwable? = null) :
    Exception(description, cause)