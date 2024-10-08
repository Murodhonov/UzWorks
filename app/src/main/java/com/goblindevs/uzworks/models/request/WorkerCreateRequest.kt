package com.goblindevs.uzworks.models.request

data class WorkerCreateRequest(
    var birthDate: String,
    var categoryId: String,
    var deadline: String,
    var districtId: String,
    var gender: Int?,
    var instagramLink: String,
    var phoneNumber: String,
    var salary: Int,
    var telegramLink: String,
    var tgUserName: String,
    var title: String,
    var workingSchedule: String,
    var workingTime: String
)