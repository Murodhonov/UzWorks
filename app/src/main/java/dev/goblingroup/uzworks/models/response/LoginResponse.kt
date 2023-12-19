package dev.goblingroup.uzworks.models.response

data class LoginResponse(
    val access: List<String>,
    val birthDate: String,
    val email: String,
    val expiration: String,
    val firstname: String,
    val gender: String,
    val lastName: String,
    val phoneNumber: String,
    val token: String,
    val userId: String
)