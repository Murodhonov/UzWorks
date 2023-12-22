package dev.goblingroup.uzworks.resource.secured_resource.worker

sealed class EditWorkerResource<T> {

    class EditLoading<T> : EditWorkerResource<T>()

    class EditSuccess<T> : EditWorkerResource<T>()

    class EditError<T : Any>(val createError: Throwable) : EditWorkerResource<T>()

}