package dev.goblingroup.uzworks.utils

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.res.Resources
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputLayout
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.database.entity.AnnouncementEntity
import dev.goblingroup.uzworks.databinding.GenderChoiceLayoutBinding
import java.text.SimpleDateFormat
import java.util.Calendar
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

fun String.dmyToIso(): String? {
    return try {
        val inputDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val inputDateObject = inputDateFormat.parse(this)

        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        outputDateFormat.format(inputDateObject!!)
    } catch (e: Exception) {
        null
    }
}

fun String.isoToDmy(): String? {
    return try {
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val inputDateObject = inputDateFormat.parse(this)

        val outputDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        outputDateFormat.format(inputDateObject!!)
    } catch (e: Exception) {
        null
    }
}

fun String.extractDateValue(dateType: String): Int {
    return try {
        val inputDateFormat = SimpleDateFormat(
            if (this.contains('-')) "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" else "dd.MM.yyyy",
            Locale.getDefault()
        )
        val parsedDate = inputDateFormat.parse(this)

        if (parsedDate != null) {
            when (dateType) {
                DateEnum.DATE.dateLabel -> {
                    val calendar = Calendar.getInstance()
                    calendar.time = parsedDate
                    calendar.get(Calendar.DAY_OF_MONTH)
                }

                DateEnum.MONTH.dateLabel -> {
                    val calendar = Calendar.getInstance()
                    calendar.time = parsedDate
                    calendar.get(Calendar.MONTH) + 1 // Month index starts from 0
                }

                DateEnum.YEAR.dateLabel -> {
                    val calendar = Calendar.getInstance()
                    calendar.time = parsedDate
                    calendar.get(Calendar.YEAR)
                }

                else -> 0
            }
        } else {
            // Return current date, month, or year if parsing fails
            val calendar = Calendar.getInstance()
            when (dateType) {
                DateEnum.DATE.dateLabel -> calendar.get(Calendar.DAY_OF_MONTH)
                DateEnum.MONTH.dateLabel -> calendar.get(Calendar.MONTH) + 1
                DateEnum.YEAR.dateLabel -> calendar.get(Calendar.YEAR)
                else -> 0
            }
        }
    } catch (e: Exception) {
        // Return current date, month, or year in case of exception
        val calendar = Calendar.getInstance()
        when (dateType) {
            DateEnum.DATE.dateLabel -> calendar.get(Calendar.DAY_OF_MONTH)
            DateEnum.MONTH.dateLabel -> calendar.get(Calendar.MONTH) + 1
            DateEnum.YEAR.dateLabel -> calendar.get(Calendar.YEAR)
            else -> 0
        }
    }
}

fun String.isStrongPassword(): Boolean {
    if (this.length < 8) return false
    if (!this.any { it.isLowerCase() }) return false
    if (!this.any { it.isDigit() }) return false
    return true
}

fun String.convertPhoneNumber(): String? {
    if (this.length != 13) return null
    val prefix = this.substring(0, 4)
    val code = this.substring(4, 6)
    val start = this.substring(6, 9)
    val middle = this.substring(9, 11)
    val end = this.substring(11)
    return "$prefix $code $start $middle $end"
}

fun String.formatPhoneNumber(backSpacePressed: Boolean): String {
    return when (this.length) {
        0, 2, 3, 4 -> {
            "+998 "
        }

        1 -> {
            "+998 $this"
        }

        5 -> {
            "${this.substring(0, 4)} ${this.last()}"
        }

        6 -> {
            "${this.substring(0, 4)} ${this.substring(4)}${controlEnd(backSpacePressed)}"
        }

        7, 8 -> {
            "${this.substring(0, 4)} ${this.substring(4, 6)} ${this.substring(6)}"
        }

        9 -> {
            "${this.substring(0, 4)} ${
                this.substring(
                    4,
                    6
                )
            } ${this.substring(6)}${controlEnd(backSpacePressed)}"
        }

        10 -> {
            "${this.substring(0, 4)} ${this.substring(4, 6)} ${this.substring(6, 9)} ${this.last()}"
        }

        11 -> {
            "${this.substring(0, 4)} ${this.substring(4, 6)} ${
                this.substring(
                    6,
                    9
                )
            } ${this.substring(9)}${controlEnd(backSpacePressed)}"
        }

        12 -> {
            "${this.substring(0, 4)} ${this.substring(4, 6)} ${
                this.substring(
                    6,
                    9
                )
            } ${this.substring(9, 11)} ${this.last()}"
        }

        13 -> {
            "${this.substring(0, 4)} ${this.substring(4, 6)} ${
                this.substring(
                    6,
                    9
                )
            } ${this.substring(9, 11)} ${this.substring(11)}"
        }

        else -> {
            "${this.substring(0, 4)} ${this.substring(4, 6)} ${
                this.substring(
                    6,
                    9
                )
            } ${this.substring(9, 11)} ${this.substring(11, 13)}"
        }
    }
}

private fun controlEnd(backSpacePressed: Boolean): String {
    return if (backSpacePressed) " " else ""
}

fun AnnouncementEntity.getImage(): Int {
    val images = mutableListOf(
        R.drawable.ic_logo_1,
        R.drawable.ic_logo_2,
        R.drawable.ic_logo_3,
        R.drawable.ic_logo_4,
        R.drawable.ic_logo_5,
        R.drawable.ic_logo_6,
        R.drawable.ic_logo_7,
        R.drawable.ic_logo_8,
        R.drawable.ic_logo_9,
        R.drawable.ic_logo_10,
    )
    return when (this.announcementType) {
        AnnouncementEnum.JOB.announcementType -> {
            images[(0 until 10).random()]
        }

        AnnouncementEnum.WORKER.announcementType -> {
            when (this.gender) {
                GenderEnum.FEMALE.label -> R.drawable.ic_female
                GenderEnum.MALE.label -> R.drawable.ic_male
                else -> {
                    R.drawable.uz_works_logo
                }
            }
        }

        else -> 0
    }
}

fun TextInputLayout.clear() {
    this.editText?.isFocusable = false
    this.editText?.isClickable = true
    this.editText?.setOnLongClickListener { true }
}

fun GenderChoiceLayoutBinding.selectMale(resources: Resources) {
    this.apply {
        maleStroke.setBackgroundResource(R.drawable.gender_stroke_selected)
        femaleStroke.setBackgroundResource(R.drawable.gender_stroke_unselected)
        maleCircle.visibility = View.VISIBLE
        femaleCircle.visibility = View.GONE
        maleTv.setTextColor(resources.getColor(R.color.black_blue))
        femaleTv.setTextColor(resources.getColor(R.color.text_color))
        maleBtn.strokeColor = resources.getColor(R.color.black_blue)
        femaleBtn.strokeColor = resources.getColor(R.color.text_color)
    }
}

fun GenderChoiceLayoutBinding.selectFemale(resources: Resources) {
    this.apply {
        femaleStroke.setBackgroundResource(R.drawable.gender_stroke_selected)
        maleStroke.setBackgroundResource(R.drawable.gender_stroke_unselected)
        femaleCircle.visibility = View.VISIBLE
        maleCircle.visibility = View.GONE
        femaleTv.setTextColor(resources.getColor(R.color.black_blue))
        maleTv.setTextColor(resources.getColor(R.color.text_color))
        femaleBtn.strokeColor = resources.getColor(R.color.black_blue)
        maleBtn.strokeColor = resources.getColor(R.color.text_color)
    }
}