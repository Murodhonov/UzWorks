package com.goblindevs.uzworks.models.request

data class ExperienceCreateRequest(
    val companyName: String,
    val description: String,
    val endDate: String,
    val position: String,
    val startDate: String
)