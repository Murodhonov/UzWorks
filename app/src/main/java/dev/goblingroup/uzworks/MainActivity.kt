package dev.goblingroup.uzworks

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.view.menu.MenuBuilder
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dev.goblingroup.uzworks.databinding.ActivityMainBinding
import dev.goblingroup.uzworks.utils.dpToPx

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val TAG = "MainActivity"

    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    private var lastId = -1
    private lateinit var bottomMenu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        binding.apply {
            setContentView(root)
            window.statusBarColor = getColor(R.color.black_blue)
            bottomBar.tag = bottomBar.visibility
            bottomBar
                .viewTreeObserver
                .addOnGlobalLayoutListener {
                    val visibility = bottomBar.visibility
                    if (bottomBar.tag as Int != visibility) {
                        Log.d(TAG, "onCreate: bottom navigation visibility changed")
                        bottomBar.tag = bottomBar.visibility
                    }
                }

            navHostFragment =
                supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
            navController = navHostFragment.navController
            setupWithNavController()

            navController.addOnDestinationChangedListener { controller, destination, arguments ->
                when (destination.id) {
                    R.id.homeFragment,
                    R.id.jobsFragment,
                    R.id.chatsListFragment,
                    R.id.profileFragment -> {
                        if (canShowAnimate()) {
                            showBottomNavigation()
                        }
                    }

                    else -> {
                        if (canHideAnimate()) {
                            hideBottomNavigation()
                        }
                    }
                }
            }

        }
    }

    @SuppressLint("RestrictedApi")
    private fun setupWithNavController() {
        binding.apply {
            val inflater = MenuInflater(this@MainActivity)
            bottomMenu = MenuBuilder(this@MainActivity)
            inflater.inflate(R.menu.bottom_menu, bottomMenu)
            bottomBar.setupWithNavController(bottomMenu, navController)
        }
    }

    private fun canHideAnimate(): Boolean {
        return lastId == R.id.homeFragment ||
                lastId == R.id.jobsFragment ||
                lastId == R.id.chatsListFragment ||
                lastId == R.id.profileFragment
    }

    private fun canShowAnimate(): Boolean {
        return lastId != R.id.homeFragment &&
                lastId != R.id.jobsFragment &&
                lastId != R.id.chatsListFragment &&
                lastId != R.id.profileFragment
    }

    private fun showBottomNavigation() {
        binding.apply {
            val showAnimation = TranslateAnimation(
                0f, 0f, bottomBar.height.toFloat() + dpToPx(10f), 0f
            )
            showAnimation.duration = 300 // Adjust the duration as needed

            showAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    // Animation start
                }

                override fun onAnimationEnd(animation: Animation) {
                    // Set visibility to VISIBLE after animation ends
                    bottomBar.visibility = View.VISIBLE
                    updateLastId(navController)
                }

                override fun onAnimationRepeat(animation: Animation) {
                    // Animation repeat
                }
            })

            bottomBar.startAnimation(showAnimation)
        }
    }

    private fun hideBottomNavigation() {
        binding.apply {
            val hideAnimation = TranslateAnimation(
                0f, 0f, 0f, bottomBar.height.toFloat() + dpToPx(10f)
            )
            hideAnimation.duration = 300 // Adjust the duration as needed

            hideAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    // Animation start
                }

                override fun onAnimationEnd(animation: Animation) {
                    // Set visibility to GONE after animation ends
                    bottomBar.visibility = View.GONE
                    updateLastId(navController)
                }

                override fun onAnimationRepeat(animation: Animation) {
                    // Animation repeat
                }
            })

            bottomBar.startAnimation(hideAnimation)
        }
    }

    private fun updateLastId(navController: NavController) {
//        val backStackEntries = navController.backQueue.toList()
//        lastId = backStackEntries[backStackEntries.size - 1].destination.id
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.getStartedFragment || navController.currentDestination?.id == R.id.homeFragment) {
            finishAffinity()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}