package dev.goblingroup.uzworks.models.request

data class WorkerEditRequest(
    val birthDate: String,
    val categoryId: String,
    val deadline: String,
    val districtId: String,
    val gender: String,
    val id: String,
    val instagramLink: String,
    val location: String,
    val phoneNumber: String,
    val salary: Int,
    val tgLink: String,
    val tgUserName: String,
    val title: String,
    val workingSchedule: String,
    val workingTime: String
)