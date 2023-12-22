package dev.goblingroup.uzworks.resource.secured_resource.region

sealed class DeleteRegionResource<T> {

    class DeleteLoading<T> : DeleteRegionResource<T>()

    class DeleteSuccess<T> : DeleteRegionResource<T>()

    class DeleteError<T : Any>(val deleteError: Throwable) : DeleteRegionResource<T>()

}