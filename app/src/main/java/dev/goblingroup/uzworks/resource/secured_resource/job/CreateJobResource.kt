package dev.goblingroup.uzworks.resource.secured_resource.job

sealed class CreateJobResource<T> {

    class CreateLoading<T> : CreateJobResource<T>()

    class CreateSuccess<T> : CreateJobResource<T>()

    class CreateError<T : Any>(val createError: Throwable) : CreateJobResource<T>()

}