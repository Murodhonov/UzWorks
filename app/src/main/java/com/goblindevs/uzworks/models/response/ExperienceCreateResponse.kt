package com.goblindevs.uzworks.models.response

data class ExperienceCreateResponse(
    val companyName: String,
    val description: String,
    val endDate: String,
    val id: String,
    val position: String,
    val startDate: String
)