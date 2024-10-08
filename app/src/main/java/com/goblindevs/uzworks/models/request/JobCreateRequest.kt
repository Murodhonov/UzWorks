package com.goblindevs.uzworks.models.request

data class JobCreateRequest(
    var benefit: String,
    var categoryId: String,
    var deadline: String,
    var districtId: String,
    var gender: Int?,
    var instagramLink: String,
    var latitude: Double,
    var longitude: Double,
    var maxAge: Int,
    var minAge: Int,
    var phoneNumber: String,
    var requirement: String,
    var salary: Int,
    var telegramLink: String,
    var tgUserName: String,
    var title: String,
    var workingSchedule: String,
    var workingTime: String
)