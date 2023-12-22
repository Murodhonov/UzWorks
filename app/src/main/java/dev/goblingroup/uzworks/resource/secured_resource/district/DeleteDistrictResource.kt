package dev.goblingroup.uzworks.resource.secured_resource.district

sealed class DeleteDistrictResource<T> {

    class DeleteLoading<T> : DeleteDistrictResource<T>()

    class DeleteSuccess<T> : DeleteDistrictResource<T>()

    class DeleteError<T : Any>(val deleteError: Throwable) : DeleteDistrictResource<T>()

}