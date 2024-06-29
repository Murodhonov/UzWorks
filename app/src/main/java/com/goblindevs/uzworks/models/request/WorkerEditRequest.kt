package com.goblindevs.uzworks.models.request

data class WorkerEditRequest(
    val birthDate: String,
    val categoryId: String,
    val deadline: String,
    val districtId: String,
    val gender: Int,
    val id: String,
    val instagramLink: String,
    val phoneNumber: String,
    val salary: Int,
    val telegramLink: String,
    val tgUserName: String,
    val title: String,
    val workingSchedule: String,
    val workingTime: String
)