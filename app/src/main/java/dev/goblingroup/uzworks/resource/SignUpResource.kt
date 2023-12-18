package dev.goblingroup.uzworks.resource

import dev.goblingroup.uzworks.models.response.SignUpResponse

sealed class SignUpResource<T> {

    class SignUpLoading<T> : SignUpResource<T>()

    class SignUpSuccess<T : Any>(val signupResponse: SignUpResponse) : SignUpResource<T>()

    class SignUpError<T : Any>(val signupError: Throwable) : SignUpResource<T>()

}
