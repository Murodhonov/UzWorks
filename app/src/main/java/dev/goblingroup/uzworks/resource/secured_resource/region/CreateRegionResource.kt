package dev.goblingroup.uzworks.resource.secured_resource.region

sealed class CreateRegionResource<T> {

    class CreateLoading<T> : CreateRegionResource<T>()

    class CreateSuccess<T> : CreateRegionResource<T>()

    class CreateError<T : Any>(val createError: Throwable) : CreateRegionResource<T>()

}