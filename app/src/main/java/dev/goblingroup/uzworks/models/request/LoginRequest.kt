package dev.goblingroup.uzworks.models.request

data class LoginRequest(
    var password: String,
    var phoneNumber: String
)