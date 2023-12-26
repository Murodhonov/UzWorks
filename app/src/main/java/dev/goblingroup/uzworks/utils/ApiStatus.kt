package dev.goblingroup.uzworks.utils

sealed class ApiStatus<T> {

    class Loading<T>: ApiStatus<T>()

    class Success<T: Any>(val response: Any?): ApiStatus<T>()

    class Error<T: Any>(val error: Throwable): ApiStatus<T>()

}