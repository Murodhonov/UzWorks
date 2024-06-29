package com.goblindevs.uzworks.models.response

data class UserResponse(
    val birthDate: String,
    val districtId: String,
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