package org.example.exceptions

class UnauthorizedException(description: String = "", cause: Throwable? = null) :
    Exception(description, cause)