package dev.goblingroup.uzworks.models.response

data class ExperienceResponse(
    val companyName: String,
    val description: String,
    val endDate: String,
    val id: String,
    val position: String,
    val startDate: String
)