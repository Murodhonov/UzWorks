package dev.goblingroup.uzworks.models.response

data class WorkerResponse(
    val birthDate: String,
    val createdBy: String,
    val createDate: String,
    val deadline: String,
    val district: District,
    val fullName: String,
    val gender: String,
    val id: String,
    val instagramLink: String,
    val isTop: Boolean,
    val jobCategory: JobCategory,
    val phoneNumber: String,
    val salary: Int,
    val status: Boolean,
    val telegramLink: String,
    val tgUserName: String,
    val title: String,
    val userName: String,
    val workingSchedule: String,
    val workingTime: String
)