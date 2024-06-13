package dev.goblingroup.uzworks.models.request

data class UserUpdateRequest(
    val birthDate: String,
    val districtId: String?,
    val districtName: String?,
    val email: String?,
    val firstName: String,
    val gender: Int,
    val id: String,
    val lastName: String,
    val mobileId: String,
    val regionName: String?
)