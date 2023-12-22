package dev.goblingroup.uzworks.resource.secured_resource.region

sealed class EditRegionResource<T> {

    class EditLoading<T> : EditRegionResource<T>()

    class EditSuccess<T> : EditRegionResource<T>()

    class EditError<T : Any>(val createError: Throwable) : EditRegionResource<T>()

}