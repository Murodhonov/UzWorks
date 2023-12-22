package dev.goblingroup.uzworks.resource.secured_resource.job

sealed class DeleteJobResource<T> {

    class DeleteLoading<T> : DeleteJobResource<T>()

    class DeleteSuccess<T> : DeleteJobResource<T>()

    class DeleteError<T : Any>(val deleteError: Throwable) : DeleteJobResource<T>()

}