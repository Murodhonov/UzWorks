package dev.goblingroup.uzworks

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
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

    private lateinit var bottomMenu: Menu

    private var lastId: Int? = null

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
                    R.id.chatsListFragment -> {
//                        showToolbarAndBottom()
                        showToolbar()
                        showBottomBar()
                    }

                    R.id.profileFragment -> {
//                        showToolbarAndBottom()
                        showBottomBar()
                        hideToolBar()
                    }

                    else -> {
                        hideToolBar()
                        hideBottomBar()
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

    private fun showBottomBar() {
        binding.apply {
            if (lastId == R.id.homeFragment ||
                lastId == R.id.jobsFragment ||
                lastId == R.id.chatsListFragment ||
                lastId == R.id.profileFragment
            ) return
            // Animation to show the bottom navigation
            val showBottomBarAnimation = TranslateAnimation(
                0f, 0f, bottomBar.height.toFloat() + dpToPx(7f), 0f
            )
            showBottomBarAnimation.duration = 300

            showBottomBarAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    // Animation start
                }

                override fun onAnimationEnd(animation: Animation) {
                    // Set visibility to VISIBLE after animation ends
                    bottomBar.visibility = View.VISIBLE
                    lastId = navController.currentDestination?.id
                }

                override fun onAnimationRepeat(animation: Animation) {
                    // Animation repeat
                }
            })

            bottomBar.startAnimation(showBottomBarAnimation)
        }
    }

    private fun showToolbar() {
        binding.apply {
            if (lastId == R.id.homeFragment ||
                lastId == R.id.jobsFragment ||
                lastId == R.id.chatsListFragment ||
                lastId == R.id.profileFragment
            ) return
            // Animation to show the toolbar
            val showToolbarAnimation = TranslateAnimation(
                0f, 0f, -toolbar.height.toFloat() - dpToPx(7f), 0f
            )
            showToolbarAnimation.duration = 300

            showToolbarAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    // Animation start
                }

                override fun onAnimationEnd(animation: Animation) {
                    // Set visibility to VISIBLE after animation ends
                    toolbar.visibility = View.VISIBLE
                    lastId = navController.currentDestination?.id
                }

                override fun onAnimationRepeat(animation: Animation) {
                    // Animation repeat
                }
            })

            toolbar.startAnimation(showToolbarAnimation)
        }
    }

    private fun hideBottomBar() {
        binding.apply {
            if (lastId != R.id.homeFragment &&
                lastId != R.id.jobsFragment &&
                lastId != R.id.chatsListFragment &&
                lastId != R.id.profileFragment
            ) return
            // Animation to hide the bottom navigation
            val hideBottomBarAnimation = TranslateAnimation(
                0f, 0f, 0f, bottomBar.height.toFloat() + dpToPx(7f)
            )
            hideBottomBarAnimation.duration = 300

            hideBottomBarAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    // Animation start
                }

                override fun onAnimationEnd(animation: Animation) {
                    // Set visibility to GONE after animation ends
                    bottomBar.visibility = View.GONE
                    lastId = navController.currentDestination?.id
                }

                override fun onAnimationRepeat(animation: Animation) {
                    // Animation repeat
                }
            })

            bottomBar.startAnimation(hideBottomBarAnimation)
        }
    }

    private fun hideToolBar() {
        binding.apply {
            if (lastId != R.id.homeFragment &&
                lastId != R.id.jobsFragment &&
                lastId != R.id.chatsListFragment &&
                lastId != R.id.profileFragment
            ) return
            // Animation to hide the toolbar
            val hideToolbarAnimation = TranslateAnimation(
                0f, 0f, 0f, -toolbar.height.toFloat() - dpToPx(7f)
            )
            hideToolbarAnimation.duration = 300

            hideToolbarAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    // Animation start
                }

                override fun onAnimationEnd(animation: Animation) {
                    // Set visibility to GONE after animation ends
                    toolbar.visibility = View.GONE
                    lastId = navController.currentDestination?.id
                }

                override fun onAnimationRepeat(animation: Animation) {
                    // Animation repeat
                }
            })

            toolbar.startAnimation(hideToolbarAnimation)
        }
    }

    private fun showToolbarAndBottom() {
        binding.apply {
            if (lastId == R.id.homeFragment ||
                lastId == R.id.jobsFragment ||
                lastId == R.id.chatsListFragment ||
                lastId == R.id.profileFragment
            ) return
            // Animation to show the bottom navigation
            val showBottomBarAnimation = TranslateAnimation(
                0f, 0f, bottomBar.height.toFloat() + dpToPx(7f), 0f
            )
            showBottomBarAnimation.duration = 300

            // Animation to show the toolbar
            val showToolbarAnimation = TranslateAnimation(
                0f, 0f, -toolbar.height.toFloat() - dpToPx(7f), 0f
            )
            showToolbarAnimation.duration = 300

            val animationSet = AnimationSet(true)
            animationSet.addAnimation(showBottomBarAnimation)
            animationSet.addAnimation(showToolbarAnimation)

            animationSet.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    // Animation start
                }

                override fun onAnimationEnd(animation: Animation) {
                    // Set visibility to VISIBLE after animation ends
                    bottomBar.visibility = View.VISIBLE
                    toolbar.visibility = View.VISIBLE
                    lastId = navController.currentDestination?.id
                }

                override fun onAnimationRepeat(animation: Animation) {
                    // Animation repeat
                }
            })

            bottomBar.startAnimation(animationSet)
            toolbar.startAnimation(animationSet)
        }
    }

    private fun hideToolbarAndBottom() {
        binding.apply {
            if (lastId != R.id.homeFragment &&
                lastId != R.id.jobsFragment &&
                lastId != R.id.chatsListFragment &&
                lastId != R.id.profileFragment
            ) return
            // Animation to hide the bottom navigation
            val hideBottomBarAnimation = TranslateAnimation(
                0f, 0f, 0f, bottomBar.height.toFloat() + dpToPx(7f)
            )
            hideBottomBarAnimation.duration = 300

            // Animation to hide the toolbar
            val hideToolbarAnimation = TranslateAnimation(
                0f, 0f, 0f, -toolbar.height.toFloat() - dpToPx(7f)
            )
            hideToolbarAnimation.duration = 300

            val animationSet = AnimationSet(true)
            animationSet.addAnimation(hideBottomBarAnimation)
            animationSet.addAnimation(hideToolbarAnimation)

            animationSet.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    // Animation start
                }

                override fun onAnimationEnd(animation: Animation) {
                    // Set visibility to GONE after animation ends
                    bottomBar.visibility = View.GONE
                    toolbar.visibility = View.GONE
                    lastId = navController.currentDestination?.id
                }

                override fun onAnimationRepeat(animation: Animation) {
                    // Animation repeat
                }
            })

            bottomBar.startAnimation(animationSet)
            toolbar.startAnimation(animationSet)
        }
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.getStartedFragment || navController.currentDestination?.id == R.id.homeFragment) {
            finishAffinity()
        } else {
            when (navController.currentDestination?.id) {
                R.id.jobsFragment,
                R.id.chatsListFragment,
                R.id.profileFragment -> {
                    navController.navigate(R.id.homeFragment)
                }

                else -> {
                    super.onBackPressed()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}