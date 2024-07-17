package com.goblindevs.uzworks.utils

enum class GenderEnum(val code: Int, val label: String) {
    MALE(0, "Male"),
    FEMALE(1, "Female"),
    UNKNOWN(2, "Unknown")
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
    KIRILL_UZB("es", "Ўзбекча"),
    LATIN_UZB("uz", "O'zbekcha"),
    RUSSIAN("ru", "Русский"),
    ENGLISH("en", "English"),
}

enum class AnnouncementEnum(val announcementType: String) {
    JOB("Job"),
    WORKER("Worker")
}

enum class PeriodEnum(val label: String) {
    SECONDS("seconds"),
    MINUTES("minutes"),
    HOURS("hours"),
    DAYS("days"),
    WEEKS("weeks"),
    MONTHS("months"),
    YEARS("years")
}

enum class ToastType {
    ERROR,
    SUCCESS
}

enum class LoginError(val errorMessage: String) {
    NOT_FOUND("Not Found"),
    VERIFY_PHONE_NUMBER("Please verify your phone number"),
    WRONG_PASSWORD("Your Password is incorrect.")
}

enum class SignUpError(val errorMessage: String) {
    PHONE_NUMBER_SYNTAX_ERROR("Syntax error with your phone number."),
    SELECT_ROLE("Please select ${UserRole.EMPLOYEE} or ${UserRole.EMPLOYER} as your role."),
    USER_EXISTS_LOGIN("This user already created. You can Login to your account."),
    SERVER_ERROR("Didn't Succeed.")
}