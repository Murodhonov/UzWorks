package dev.goblingroup.uzworks.models.response

data class AnnouncementResponse(
    val id: String,
    val categoryName: String,
    val districtName: String,
    val regionName: String,
    val gender: Int,
    val salary: Int,
    val title: String,
    val announcementType: String,
    val isTop: Boolean,
    val pictureResId: Int
)