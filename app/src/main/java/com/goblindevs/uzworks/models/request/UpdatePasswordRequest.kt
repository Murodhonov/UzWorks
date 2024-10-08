package com.goblindevs.uzworks.models.request

data class UpdatePasswordRequest(
    val confirmPassword: String,
    val newPassword: String,
    val oldPassword: String,
    val userId: String
)