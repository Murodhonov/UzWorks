package dev.goblingroup.uzworks.resource.secured_resource.district

sealed class EditDistrictResource<T> {

    class EditLoading<T> : EditDistrictResource<T>()

    class EditSuccess<T> : EditDistrictResource<T>()

    class EditError<T : Any>(val createError: Throwable) : EditDistrictResource<T>()

}