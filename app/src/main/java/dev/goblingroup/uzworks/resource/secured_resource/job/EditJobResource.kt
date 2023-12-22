package dev.goblingroup.uzworks.resource.secured_resource.job

sealed class EditJobResource<T> {

    class EditLoading<T> : EditJobResource<T>()

    class EditSuccess<T> : EditJobResource<T>()

    class EditError<T : Any>(val createError: Throwable) : EditJobResource<T>()

}