package dev.goblingroup.uzworks.utils

import android.content.res.Resources
import android.util.TypedValue
import androidx.navigation.NavOptions
import dev.goblingroup.uzworks.R
import java.text.SimpleDateFormat
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
    val sdfInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val date: Date = sdfInput.parse(dateString) ?: return 0 // Return 0 if parsing fails
    val calendar = Calendar.getInstance()
    calendar.time = date

    return when (dateType) {
        DateEnum.DATE.dateLabel -> {
            calendar.get(Calendar.DAY_OF_MONTH)
        }

        DateEnum.MONTH.dateLabel -> {
            calendar.get(Calendar.MONTH)
        }

        DateEnum.YEAR.dateLabel -> {
            calendar.get(Calendar.YEAR)
        }

        else -> 0
    }
}