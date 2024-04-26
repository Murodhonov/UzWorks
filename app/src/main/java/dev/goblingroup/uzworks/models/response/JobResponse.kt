package dev.goblingroup.uzworks.models.response

data class JobResponse(
    val benefit: String,
    val categoryName: String,
    val createDate: String,
    val deadline: String,
    val districtName: String,
    val gender: Int,
    val id: String,
    val instagramLink: String,
    val isTop: Boolean,
    val latitude: Double,
    val longitude: Double,
    val maxAge: Int,
    val minAge: Int,
    val phoneNumber: String,
    val regionName: String,
    val requirement: String,
    val salary: Int,
    val telegramLink: String,
    val tgUserName: String,
    val title: String,
    val workingSchedule: String,
    val workingTime: String
)