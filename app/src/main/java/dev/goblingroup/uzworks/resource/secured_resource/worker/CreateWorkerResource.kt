package dev.goblingroup.uzworks.resource.secured_resource.worker

sealed class CreateWorkerResource<T> {

    class CreateLoading<T> : CreateWorkerResource<T>()

    class CreateSuccess<T> : CreateWorkerResource<T>()

    class CreateError<T : Any>(val createError: Throwable) : CreateWorkerResource<T>()

}