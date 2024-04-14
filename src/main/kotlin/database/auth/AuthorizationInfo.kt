package org.example.database.auth

import kotlinx.serialization.Serializable

@Serializable
class AuthorizationInfo(val login: Login, val password: Password)
