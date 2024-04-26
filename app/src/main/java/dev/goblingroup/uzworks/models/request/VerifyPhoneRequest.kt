package dev.goblingroup.uzworks.models.request

data class VerifyPhoneRequest(
    val code: String,
    val phoneNumber: String
)