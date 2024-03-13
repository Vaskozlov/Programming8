package exceptions

class FileWriteException(description: String = "", cause: Throwable? = null) 
    : Exception(description, cause)
