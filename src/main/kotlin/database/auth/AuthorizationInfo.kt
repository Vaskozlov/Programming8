package database.auth

import kotlinx.serialization.Serializable
import org.example.database.auth.Password

@Serializable
class AuthorizationInfo(val login: Login, val password: Password)
