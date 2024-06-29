package com.goblindevs.uzworks.models.response

data class SignUpResponse(
    val firstName: String,
    val id: String,
    val lastName: String,
    val phoneNumber: String,
    val roles: List<String>
)