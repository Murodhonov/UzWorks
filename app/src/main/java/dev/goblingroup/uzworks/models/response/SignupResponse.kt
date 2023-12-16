package dev.goblingroup.uzworks.models.response

data class SignupResponse(
    val errors: List<Any>,
    val succeeded: Boolean
)