package dev.goblingroup.uzworks.models.request

data class ResetPasswordRequest(
    val confirmPassword: String,
    val newPassword: String,
    val oldPassword: String,
    val userId: String
)