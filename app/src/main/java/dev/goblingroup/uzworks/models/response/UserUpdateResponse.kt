package dev.goblingroup.uzworks.models.response

data class UserUpdateResponse(
    val birthDate: String,
    val districtId: String?,
    val districtName: String?,
    val email: String,
    val firstName: String,
    val gender: Int,
    val id: String,
    val lastName: String,
    val mobileId: String,
    val phoneNumber: String,
    val regionName: String?
)