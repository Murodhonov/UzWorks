package com.goblindevs.uzworks.models.request

data class JobEditRequest(
    val benefit: String,
    val categoryId: String,
    val deadline: String,
    val districtId: String,
    val gender: Int,
    val id: String,
    val instagramLink: String,
    val latitude: Double,
    val longitude: Double,
    val maxAge: Int,
    val minAge: Int,
    val phoneNumber: String,
    val requirement: String,
    val salary: Int,
    val telegramLink: String,
    val tgUserName: String,
    val title: String,
    val workingSchedule: String,
    val workingTime: String
)