package dev.goblingroup.uzworks.vm

import dev.goblingroup.uzworks.models.response.ErrorResponse

sealed class AuthApiStatus<T> {

    class Loading<T>: AuthApiStatus<T>()

    class Success<T: Any>(val response: T?): AuthApiStatus<T>()

    class Error<T: Any>(val errorResponse: ErrorResponse): AuthApiStatus<T>()

}