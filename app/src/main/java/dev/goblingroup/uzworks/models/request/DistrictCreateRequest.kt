package dev.goblingroup.uzworks.models.request

data class DistrictCreateRequest(
    val name: String,
    val regionId: String
)