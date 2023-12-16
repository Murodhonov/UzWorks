package dev.goblingroup.uzworks.resource

import dev.goblingroup.uzworks.models.response.LoginResponse

sealed class LoginResource<T> {

    class LoginLoading<T> : LoginResource<T>()

    class LoginSuccess<T : Any>(val loginResponse: LoginResponse) : LoginResource<T>()

    class LoginError<T : Any>(val loginError: Throwable) : LoginResource<T>()

}