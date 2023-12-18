package dev.goblingroup.uzworks.models.response

data class SignUpResponse(
    val errors: List<Any>,
    val succeeded: Boolean
)