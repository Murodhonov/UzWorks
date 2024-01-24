package dev.goblingroup.uzworks.models.response

data class WorkerResponse(
    val birthDate: String,
    val categoryId: String,
    val createDate: String,
    val createdBy: String,
    val deadline: String,
    val districtId: String,
    val fullName: String,
    val gender: String,
    val id: String,
    val instagramLink: String,
    val location: String,
    val phoneNumber: String,
    val salary: Int,
    val telegramLink: String,
    val tgUserName: String,
    val title: String,
    val userName: String,
    val workingSchedule: String,
    val workingTime: String
)