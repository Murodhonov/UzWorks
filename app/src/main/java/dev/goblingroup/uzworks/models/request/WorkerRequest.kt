package dev.goblingroup.uzworks.models.request

data class WorkerRequest(
    var birthDate: String,
    var categoryId: String,
    var deadline: String,
    var districtId: String,
    var gender: String,
    var instagramLink: String,
    var location: String,
    var phoneNumber: String,
    var salary: Int,
    var telegramLink: String,
    var tgUserName: String,
    var title: String,
    var workingSchedule: String,
    var workingTime: String
)