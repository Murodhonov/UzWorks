package dev.goblingroup.uzworks.models.response

class JobResponse {
    var benefit: String? = null
    var categoryId: String? = null
    var deadline: String? = null
    var districtId: String? = null
    var gender: String? = null
    var id: String? = null
    var instagramLink: String? = null
    var latitude: Double? = null
    var longitude: Double? = null
    var maxAge: Int? = null
    var minAge: Int? = null
    var phoneNumber: String? = null
    var requirement: String? = null
    var salary: Int? = null
    var telegramLink: String? = null
    var tgUserName: String? = null
    var title: String? = null
    var workingSchedule: String? = null
    var workingTime: String? = null

    constructor(
        benefit: String?,
        categoryId: String?,
        deadline: String?,
        districtId: String?,
        gender: String?,
        id: String?,
        instagramLink: String?,
        latitude: Double?,
        longitude: Double?,
        maxAge: Int?,
        minAge: Int?,
        phoneNumber: String?,
        requirement: String?,
        salary: Int?,
        telegramLink: String?,
        tgUserName: String?,
        title: String?,
        workingSchedule: String?,
        workingTime: String?
    ) {
        this.benefit = benefit
        this.categoryId = categoryId
        this.deadline = deadline
        this.districtId = districtId
        this.gender = gender
        this.id = id
        this.instagramLink = instagramLink
        this.latitude = latitude
        this.longitude = longitude
        this.maxAge = maxAge
        this.minAge = minAge
        this.phoneNumber = phoneNumber
        this.requirement = requirement
        this.salary = salary
        this.telegramLink = telegramLink
        this.tgUserName = tgUserName
        this.title = title
        this.workingSchedule = workingSchedule
        this.workingTime = workingTime
    }

    constructor()
}