package dev.goblingroup.uzworks.utils

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavOptions
import com.google.android.material.textfield.TextInputLayout
import dev.goblingroup.uzworks.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun turnSwitchOff(
    resources: Resources,
    track: ConstraintLayout,
    thumb: View,
    onAnimationComplete: () -> Unit
) {
    // 1st Animation: Change track background from switch_track_on to switch_track_off
    val trackBackgroundAnimator = ValueAnimator()

    val trackStartColor = resources.getColor(R.color.green)
    val trackEndColor = resources.getColor(R.color.white_blue)

    trackBackgroundAnimator.setIntValues(trackStartColor, trackEndColor)
    trackBackgroundAnimator.setEvaluator(ArgbEvaluator())
    trackBackgroundAnimator.addUpdateListener { valueAnimator ->
        val color = valueAnimator.animatedValue as Int
        val backgroundDrawable = track.background as GradientDrawable
        backgroundDrawable.setColor(color)
    }

    // 2nd Animation: Change thumb background from switch_thumb_on to switch_thumb_off
    val thumbBackgroundAnimator = ValueAnimator()

    val thumbStartColor = resources.getColor(R.color.white)
    val thumbEndColor = resources.getColor(R.color.black_blue)

    thumbBackgroundAnimator.setIntValues(thumbStartColor, thumbEndColor)
    thumbBackgroundAnimator.setEvaluator(ArgbEvaluator())
    thumbBackgroundAnimator.addUpdateListener { valueAnimator ->
        val color = valueAnimator.animatedValue as Int
        val backgroundDrawable = thumb.background as GradientDrawable
        backgroundDrawable.setColor(color)
    }

    // 3rd Animation: Move thumb to the left of track
    val thumbWidth = thumb.width
    val trackWidth = track.width
    val thumbAnimator = ObjectAnimator.ofFloat(
        thumb,
        "translationX",
        0f,
        -(trackWidth - thumbWidth - thumbWidth / 2.0).toFloat()
    )
    thumbAnimator.interpolator = AccelerateDecelerateInterpolator()

    // Create an AnimatorSet to play all animations simultaneously
    val animatorSet = AnimatorSet()
    animatorSet.playTogether(
        trackBackgroundAnimator,
        thumbBackgroundAnimator,
        thumbAnimator
    )

    animatorSet.duration = 500 // Set the duration in milliseconds

    animatorSet.addListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(p0: Animator) {

        }

        override fun onAnimationEnd(p0: Animator) {
            onAnimationComplete.invoke()
        }

        override fun onAnimationCancel(p0: Animator) {

        }

        override fun onAnimationRepeat(p0: Animator) {

        }

    })

    animatorSet.start()
}

fun turnSwitchOn(
    resources: Resources,
    track: ConstraintLayout,
    thumb: View,
    onAnimationComplete: () -> Unit
) {
    // 1st Animation: Change thumb background from switch_thumb_off to switch_thumb_on
    val thumbBackgroundAnimator = ValueAnimator()

    val thumbStartColor = resources.getColor(R.color.black_blue) // Color of switch_thumb_off
    val thumbEndColor = resources.getColor(R.color.white) // Color of switch_thumb_on

    thumbBackgroundAnimator.setIntValues(thumbStartColor, thumbEndColor)
    thumbBackgroundAnimator.setEvaluator(ArgbEvaluator())
    thumbBackgroundAnimator.addUpdateListener { valueAnimator ->
        val color = valueAnimator.animatedValue as Int
        val backgroundDrawable = thumb.background as GradientDrawable
        backgroundDrawable.setColor(color)
    }

    // 2nd Animation: Change track background from switch_track_off to switch_track_on
    val trackBackgroundAnimator = ValueAnimator()
    val trackStartColor = resources.getColor(R.color.white_blue) // Color of switch_track_off
    val trackEndColor = resources.getColor(R.color.green)   // Color of switch_track_on
    trackBackgroundAnimator.setIntValues(trackStartColor, trackEndColor)
    trackBackgroundAnimator.setEvaluator(ArgbEvaluator())
    trackBackgroundAnimator.addUpdateListener { valueAnimator ->
        val color = valueAnimator.animatedValue as Int
        val backgroundDrawable = track.background as GradientDrawable
        backgroundDrawable.setColor(color)
    }

    // 3rd Animation: Move thumb to the right of track
    val thumbWidth = thumb.width
    val trackWidth = track.width
    val thumbAnimator = ObjectAnimator.ofFloat(
        thumb,
        "translationX",
        -(trackWidth - thumbWidth - thumbWidth / 2.0).toFloat(),
        0f
    )
    thumbAnimator.interpolator = AccelerateDecelerateInterpolator()

    // Create an AnimatorSet to play all animations simultaneously
    val animatorSet = AnimatorSet()
    animatorSet.playTogether(thumbBackgroundAnimator, trackBackgroundAnimator, thumbAnimator)

    animatorSet.duration = 500 // Set the duration in milliseconds

    animatorSet.addListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(p0: Animator) {

        }

        override fun onAnimationEnd(p0: Animator) {
            onAnimationComplete.invoke()
        }

        override fun onAnimationCancel(p0: Animator) {

        }

        override fun onAnimationRepeat(p0: Animator) {

        }

    })

    animatorSet.start()
}

@RequiresApi(Build.VERSION_CODES.M)
fun EditText.showHidePassword(
    context: Context,
    eyeIcon: ImageView
) {
    if (this.transformationMethod == PasswordTransformationMethod.getInstance()) {
        /**
         * from password mode to simple mode
         * should open eyes
         */
        this.transformationMethod = HideReturnsTransformationMethod.getInstance()
        this.setSelection(this.text.toString().length)
        this.setTextColor(context.getColor(R.color.black_blue))
        eyeIcon.setImageResource(R.drawable.ic_open_eye)
    } else {
        /**
         * from simple mode to password mode
         * should close eyes
         */
        this.transformationMethod = PasswordTransformationMethod.getInstance()
        this.setSelection(this.text.toString().length)
        this.setTextColor(context.getColor(R.color.black_blue_60))
        eyeIcon.setImageResource(R.drawable.ic_closed_eye)
    }
}

fun Float.dpToPx(): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )
}

fun TextInputLayout.splitFullName(): Pair<String?, String?> {
    val fullName = this.editText?.text.toString().trim()
    val names = fullName.split(" ")

    return when {
        names.size >= 2 -> {
            val firstName = names[0]
            val lastName = names[1]
            Pair(firstName, lastName)
        }
        else -> Pair(null, null)
    }
}

fun dateToString(date: Date): String {
    val formatter = SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        Locale.getDefault()
    )
    return formatter.format(date.time)
}

fun TextView.stringToDate(dateType: String): Int {
    val possibleDateFormats = arrayOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "dd.MM.yyyy"
    )

    val date = possibleDateFormats.mapNotNull { format ->
        try {
            SimpleDateFormat(format, Locale.getDefault()).parse(this.text.toString())
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

fun TextView.stringDateToString(): String {
    return try {
        // Parse input string to Date object
        val inputDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val inputDateObject = inputDateFormat.parse(this.text.toString())

        // Format Date object to the desired string format
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputDate = outputDateFormat.format(inputDateObject!!)

        outputDate
    } catch (e: Exception) {
        "Invalid date format. Please provide date in dd.MM.yyyy format."
    }
}

fun String.isStrongPassword(): Boolean {
    if (this.length < 8) return false
    if (!this.any { it.isLowerCase() }) return false
    if (!this.any { it.isDigit() }) return false
    return true
}