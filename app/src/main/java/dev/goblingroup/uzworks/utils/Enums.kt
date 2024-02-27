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

enum class LanguageEnum(val code: String, val languageName: String) {
    KIRILL_UZB("cyr", "Ўзбекча"),
    LATIN_UZB("uz", "O'zbekcha"),
    RUSSIAN("ru", "Русский"),
    ENGLISH("en", "English"),
}