package dev.goblingroup.uzworks.vm

import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.utils.dpToPx
import me.ibrahimsn.lib.SmoothBottomBar
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private var lastId: Int? = null

    fun showBottomBar(
        bottomBar: SmoothBottomBar,
        destinationId: Int
    ) {
        if (lastId == R.id.homeFragment ||
            lastId == R.id.announcementsFragment ||
            lastId == R.id.chatsListFragment ||
            lastId == R.id.profileFragment
        ) return

        val showBottomBarAnimation = TranslateAnimation(
            0f, 0f, bottomBar.height.toFloat() + 7f.dpToPx(), 0f
        )
        showBottomBarAnimation.duration = 300

        showBottomBarAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                bottomBar.visibility = View.VISIBLE
                lastId = destinationId
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

        bottomBar.startAnimation(showBottomBarAnimation)
    }

}