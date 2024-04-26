package dev.goblingroup.uzworks.models.request

data class SignUpRequest(
    val confirmPassword: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val phoneNumber: String,
    val role: String
)
