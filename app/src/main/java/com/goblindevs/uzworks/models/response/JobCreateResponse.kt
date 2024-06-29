package com.goblindevs.uzworks.models.response

data class JobCreateResponse(
    val title: String,
    val salary: Int,
    val gender: String,
    val workingTime: String,
    val workingSchedule: String,
    val deadline: String,
    val telegramLink: String,
    val instagramLink: String,
    val tgUserName: String,
    val phoneNumber: String,
    val benefit: String,
    val requirement: String,
    val minAge: Int,
    val maxAge: Int,
    val latitude: Double,
    val longitude: Double,
    val categoryId: String,
    val districtId: String
)