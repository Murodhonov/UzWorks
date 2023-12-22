package dev.goblingroup.uzworks.resource.secured_resource.district

sealed class CreateDistrictResource<T> {

    class CreateLoading<T> : CreateDistrictResource<T>()

    class CreateSuccess<T> : CreateDistrictResource<T>()

    class CreateError<T : Any>(val createError: Throwable) : CreateDistrictResource<T>()

}