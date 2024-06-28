package dev.goblingroup.uzworks.models.response

data class JobResponse(
    val benefit: String,
    val createDate: String,
    val deadline: String,
    val district: District,
    val gender: String,
    val id: String,
    val instagramLink: String,
    val isTop: Boolean,
    val jobCategory: JobCategory,
    val latitude: Double,
    val longitude: Double,
    val maxAge: Int,
    val minAge: Int,
    val phoneNumber: String,
    val requirement: String,
    val salary: Int,
    val status: Boolean,
    val telegramLink: String,
    val tgUserName: String,
    val title: String,
    val workingSchedule: String,
    val workingTime: String
)