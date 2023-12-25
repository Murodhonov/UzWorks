package dev.goblingroup.uzworks.resource.district

import dev.goblingroup.uzworks.models.response.DistrictResponse

sealed class DistrictResource<T> {

    class DistrictLoading<T> : DistrictResource<T>()

    class DistrictSuccess<T: Any>(val districtList: List<DistrictResponse>) : DistrictResource<T>()

    class DistrictError<T : Any>(val loadError: Throwable) : DistrictResource<T>()

}