package com.goblindevs.uzworks.models.response

data class UpdatePasswordResponse(
    val confirmPassword: String,
    val newPassword: String,
    val oldPassword: String,
    val userId: String
)