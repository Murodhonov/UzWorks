package com.goblindevs.uzworks.models.request

data class LoginRequest(
    var password: String,
    var phoneNumber: String
)