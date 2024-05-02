package dev.goblingroup.uzworks.models.response

data class WorkerCreateResponse(
    val id: String,
    val createdBy: String,
    val createDate: String,
    val deadline: String,
    val birthDate: String,
    val isTop: Boolean,
    val status: Boolean,
    val fullName: String,
    val userName: String,
    val title: String,
    val salary: Int,
    val gender: String,
    val workingTime: String,
    val workingSchedule: String,
    val telegramLink: String,
    val instagramLink: String,
    val tgUserName: String,
    val phoneNumber: String,
    val regionName: String,
    val districtName: String,
    val categoryName: String
)