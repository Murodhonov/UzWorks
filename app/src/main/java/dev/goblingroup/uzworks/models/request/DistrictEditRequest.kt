package dev.goblingroup.uzworks.models.request

data class DistrictEditRequest(
    val id: String,
    val name: String,
    val regionId: String
)