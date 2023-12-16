package dev.goblingroup.uzworks.resource

import dev.goblingroup.uzworks.models.response.SignupResponse

sealed class SignupResource<T> {

    class SignupLoading<T> : SignupResource<T>()

    class SignupSuccess<T : Any>(val signupResponse: SignupResponse) : SignupResource<T>()

    class SignupError<T : Any>(val signupError: Throwable) : SignupResource<T>()

}
