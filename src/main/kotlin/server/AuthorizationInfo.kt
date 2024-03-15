package server

import kotlinx.serialization.Serializable

@Serializable
data class AuthorizationInfo(val login: String, val password: String)
