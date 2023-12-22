package dev.goblingroup.uzworks.resource.secured_resource.worker

sealed class DeleteWorkerResource<T> {

    class DeleteLoading<T> : DeleteWorkerResource<T>()

    class DeleteSuccess<T> : DeleteWorkerResource<T>()

    class DeleteError<T : Any>(val deleteError: Throwable) : DeleteWorkerResource<T>()

}