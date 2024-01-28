package dev.goblingroup.uzworks.utils

enum class GenderEnum(val label: String) {
    MALE("Male"),
    FEMALE("FEMALE")
}

enum class UserRole(val roleName: String) {
    EMPLOYEE("Employee"),
    EMPLOYER("Employer"),
    SUPER_ADMIN("SuperAdmin"),
    NEW_USER("NewUser"),
    SUPERVISOR("Supervisor")
}

enum class DateEnum(val dateLabel: String) {
    DATE("Date"),
    MONTH("Month"),
    YEAR("Year")
}

enum class LanguageEnum(val language: String) {
    KIRILL_UZB("Ўзбекча"),
    LATIN_UZB("O’zbekcha"),
    RU("Русский"),
    EN("English")
}

enum class AdminTabsEnum(val tabTitle: String) {
    DISTRICT("District"),
    JOB("Job"),
    JOB_CATEGORY("Job category"),
    REGION("Region"),
    WORKER("Worker")
}