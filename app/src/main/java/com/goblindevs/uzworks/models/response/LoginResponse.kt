package com.goblindevs.uzworks.models.response

data class LoginResponse(
    val birthDate: String,
    val expiration: String,
    val firstName: String,
    val gender: Int?,
    val id: String,
    val lastName: String,
    val phoneNumber: String,
    val roles: List<String>,
    val token: String
)