package dev.goblingroup.uzworks.models.response

data class UserUpdateResponse(
    val birthDate: String,
    val email: String,
    val firstName: String,
    val gender: String,
    val id: String,
    val lastName: String,
    val mobileId: String,
    val phoneNumber: String,
    val userName: String
)