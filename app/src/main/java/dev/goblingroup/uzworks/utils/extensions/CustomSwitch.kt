package dev.goblingroup.uzworks.utils.extensions

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.res.Resources
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import dev.goblingroup.uzworks.R

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

    animatorSet.addListener(object : AnimatorListener {
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

    animatorSet.addListener(object : AnimatorListener {
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