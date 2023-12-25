package dev.goblingroup.uzworks.resource.district

import dev.goblingroup.uzworks.models.response.DistrictResponse

sealed class DistrictByIdResource<T> {

    class Loading<T> : DistrictByIdResource<T>()

    class Success<T : Any>(val districtResponse: DistrictResponse) : DistrictByIdResource<T>()

    class Error<T : Any>(val error: Throwable) : DistrictByIdResource<T>()

}