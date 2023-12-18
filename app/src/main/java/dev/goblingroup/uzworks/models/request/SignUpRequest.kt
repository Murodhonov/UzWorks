package dev.goblingroup.uzworks.models.request

data class SignUpRequest(
    var username: String,
    var password: String,
    var confirmPassword: String,
    var firstName: String,
    var lastName: String,
    var role: String,
)
