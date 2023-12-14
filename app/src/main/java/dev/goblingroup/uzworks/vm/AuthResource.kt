package dev.goblingroup.uzworks.vm

import dev.goblingroup.uzworks.models.response.LoginResponse

sealed class AuthResource<T> {

    class AuthLoading<T> : AuthResource<T>()

    class AuthSuccess<T : Any>(val loginResponse: LoginResponse) : AuthResource<T>()

    class AuthError<T : Any>(val authError: Throwable) : AuthResource<T>()

}