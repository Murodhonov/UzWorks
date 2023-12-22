package dev.goblingroup.uzworks.models.request

data class JobCategoryEditRequest(
    val description: String,
    val id: String,
    val title: String
)