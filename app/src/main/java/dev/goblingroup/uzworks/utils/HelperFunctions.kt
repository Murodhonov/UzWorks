package dev.goblingroup.uzworks.utils

import android.content.res.Resources
import android.util.TypedValue
import androidx.navigation.NavOptions
import dev.goblingroup.uzworks.R

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