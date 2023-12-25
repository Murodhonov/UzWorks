package dev.goblingroup.uzworks.resource.district

import dev.goblingroup.uzworks.models.response.DistrictResponse

sealed class DistrictByRegionIdResource<T> {

    class Loading<T> : DistrictByRegionIdResource<T>()

    class Success<T : Any>(val districtList: List<DistrictResponse>) :
        DistrictByRegionIdResource<T>()

    class Error<T : Any>(val error: Throwable) : DistrictByRegionIdResource<T>()

}