package com.goblindevs.uzworks.models.request

data class ExperienceEditRequest(
    val companyName: String,
    val description: String,
    val endDate: String,
    val id: String,
    val position: String,
    val startDate: String
)