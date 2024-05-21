package dev.goblingroup.uzworks.utils

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.GenderChoiceLayoutBinding
import okhttp3.ResponseBody
import org.json.JSONObject
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

        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        outputDateFormat.format(inputDateObject!!)
    } catch (e: Exception) {
        null
    }
}

fun String.isoToDmy(): String? {
    return try {
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
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
            if (this.contains('-')) "yyyy-MM-dd'T'HH:mm:ss" else "dd.MM.yyyy",
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

fun String.convertPhoneNumber(): String {
    val prefix = "+${this.substring(0, 3)}"
    val code = this.substring(3, 5)
    val start = this.substring(5, 8)
    val middle = this.substring(8, 10)
    val end = this.substring(10)
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

fun String.formatSalary(): String {
    return when (this.length) {
        0 -> ""
        in 1..3 -> this
        in 4..6 -> {
            "${this.substring(0, this.length - 3)} ${this.substring(this.length - 3)}"
        }

        in 7..9 -> {
            "${this.substring(0, this.length - 6)} ${
                this.substring(
                    this.length - 6,
                    this.length - 3
                )
            } ${this.substring(this.length - 3)}"
        }

        else -> {
            "${this.substring(0, this.length - 7)} ${
                this.substring(
                    this.length - 7,
                    this.length - 4
                )
            } ${this.substring(this.length - 4, 9)}"
        }
    }
}

fun String.formatTgUsername(): String {
    return if (this.isEmpty() || this.length == 1) "@"
    else {
        if (this.startsWith("@")) "@${this.substring(1)}"
        else "@$this"
    }
}

fun getImage(announcementType: String, gender: String, position: Int): Int {
    return when (announcementType) {
        AnnouncementEnum.JOB.announcementType -> {
            mutableListOf(
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
            )[position]
        }

        AnnouncementEnum.WORKER.announcementType -> {
            when (gender) {
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

fun String.timeAgo(): Pair<Int, String> {
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
    val then = format.parse(this) ?: return Pair(0, "")

    val now = Date()
    val diffMs = now.time - then.time

    val diffSeconds = diffMs / 1000
    val diffMinute = diffSeconds / 60
    val diffHour = diffMinute / 60
    val diffDay = diffHour / 24
    val diffWeek = diffDay / 7
    val diffMonth = diffDay / 30
    val diffYear = diffMonth / 12

    return when {
        diffSeconds < 60 -> Pair(diffSeconds.toInt(), PeriodEnum.SECONDS.label)
        diffMinute < 60 -> Pair(diffMinute.toInt(), PeriodEnum.MINUTES.label)
        diffHour < 24 -> Pair(diffHour.toInt(), PeriodEnum.HOURS.label)
        diffDay < 7 -> Pair(diffDay.toInt(), PeriodEnum.DAYS.label)
        diffWeek < 5 -> if (diffDay < 30) {
            Pair(diffWeek.toInt(), PeriodEnum.WEEKS.label)
        } else {
            Pair(diffMonth.toInt(), PeriodEnum.MONTHS.label)
        }

        else -> if (diffMonth < 12) {
            Pair(diffMonth.toInt(), PeriodEnum.MONTHS.label)
        } else {
            Pair(diffYear.toInt(), PeriodEnum.YEARS.label)
        }
    }
}

fun TextInputLayout.isEmpty(): Boolean = this.editText?.text.toString().isEmpty()

fun EditText.setFocus(fragmentActivity: FragmentActivity) {
    this.requestFocus()
    val inputMethodManager = fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    this.setSelection(this.text.toString().length)
}

fun EditText.addCodeTextWatcher(
    fragmentActivity: FragmentActivity,
    previousInput: EditText?,
    nextInput: EditText?
) {
    setOnKeyListener { _, keyCode, event ->
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9 -> {
                    if (text.isNotEmpty()) {
                        nextInput?.setText((keyCode - 7).toString())
                    }
                    nextInput?.setFocus(fragmentActivity)
                }
                KeyEvent.KEYCODE_DEL -> {
                    previousInput?.setFocus(fragmentActivity)
                }
            }
        }
        false
    }

    setOnEditorActionListener { _, actionId, _ ->
        when (actionId) {
            EditorInfo.IME_ACTION_DONE -> {
                val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
                true
            }
            EditorInfo.IME_ACTION_NEXT -> {
                nextInput?.setFocus(fragmentActivity)
                true
            }
            else -> {
                false
            }
        }
    }
}

fun ResponseBody.extractErrorMessage(): String? {
    return try {
        val jsonObject = JsonParser().parse(this.string()) as? JsonObject
        jsonObject?.get("error")?.asString
    } catch (e: Exception) {
        // Handle parsing error
        null
    }
}