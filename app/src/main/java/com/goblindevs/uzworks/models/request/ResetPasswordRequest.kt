package com.goblindevs.uzworks.models.request

data class ResetPasswordRequest(
    val code: String,
    val newPassword: String,
    val phoneNumber: String
)