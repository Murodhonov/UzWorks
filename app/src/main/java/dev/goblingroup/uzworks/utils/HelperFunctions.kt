package dev.goblingroup.uzworks.utils

import android.content.res.Resources
import android.os.Build
import android.util.TypedValue
import androidx.annotation.RequiresApi
import androidx.navigation.NavOptions
import dev.goblingroup.uzworks.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun dpToPx(distance: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        distance,
        Resources.getSystem().displayMetrics
    )
}

fun getNavOptions(): NavOptions {
    return NavOptions.Builder()
        .setEnterAnim(R.anim.enter)
        .setExitAnim(R.anim.exit)
        .setPopEnterAnim(R.anim.pop_enter)
        .setPopExitAnim(R.anim.pop_exit)
        .build()
}

fun splitFullName(fullName: String): Pair<String, String> {
    val names = fullName.trim().split(" ")

    return when (names.size) {
        2 -> {
            val firstName = names[0]
            val lastName = names[1]
            Pair(firstName, lastName)
        }

        else -> Pair("", "")
    }
}

fun dateToString(date: Date): String {
    val formatter = SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        Locale.getDefault()
    )
    return formatter.format(date.time)
}

fun stringToDate(dateString: String, dateType: String): Int {
    val possibleDateFormats = arrayOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "dd.MM.yyyy"
    )

    val date = possibleDateFormats.mapNotNull { format ->
        try {
            SimpleDateFormat(format, Locale.getDefault()).parse(dateString)
        } catch (e: ParseException) {
            null
        }
    }.firstOrNull() ?: Date() // Use current date if parsing fails

    val calendar = Calendar.getInstance()
    calendar.time = date

    return when (dateType) {
        DateEnum.DATE.dateLabel -> calendar.get(Calendar.DAY_OF_MONTH)
        DateEnum.MONTH.dateLabel -> calendar.get(Calendar.MONTH)
        DateEnum.YEAR.dateLabel -> calendar.get(Calendar.YEAR)
        else -> 0
    }
}

fun convertDateFormat(dateString: String): String {
    return try {
        // Parse input string to Date object
        val inputDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val inputDateObject = inputDateFormat.parse(dateString)

        // Format Date object to the desired string format
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputDate = outputDateFormat.format(inputDateObject!!)

        outputDate
    } catch (e: Exception) {
        "Invalid date format. Please provide date in dd.MM.yyyy format."
    }
}