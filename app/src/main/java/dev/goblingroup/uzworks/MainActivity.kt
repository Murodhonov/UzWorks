package dev.goblingroup.uzworks

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.databinding.ActivityMainBinding
import dev.goblingroup.uzworks.utils.LanguageManager
import dev.goblingroup.uzworks.utils.dpToPx
import dev.goblingroup.uzworks.utils.getNavOptions
import dev.goblingroup.uzworks.vm.MainViewModel
import dev.goblingroup.uzworks.vm.SecurityViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    private lateinit var bottomMenu: Menu

    private var lastId: Int? = null

    private val securityViewModel: SecurityViewModel by viewModels()

    private val mainViewModel: MainViewModel by viewModels()

    private val connectivityReceiver = ConnectivityBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        binding.apply {
            registerReceiver(
                connectivityReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
            if (securityViewModel.getLanguageCode() != null) {
                LanguageManager.setLanguage(securityViewModel.getLanguageCode().toString(), this@MainActivity)
            }
            setContentView(root)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.statusBarColor = getColor(R.color.black_blue)
            }

            navHostFragment =
                supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
            navController = navHostFragment.navController
            setupWithNavController()

            navController.addOnDestinationChangedListener { controller, destination, arguments ->
                when (destination.id) {
                    R.id.homeFragment,
                    R.id.announcementsFragment,
                    R.id.chatsListFragment -> {
//                        showToolbar()
//                        showBottomBar()
                        toolbar.visibility = View.VISIBLE
                        bottomBar.visibility = View.VISIBLE
                    }

                    R.id.profileFragment -> {
//                        showBottomBar()
//                        hideToolBar()
                        bottomBar.visibility = View.VISIBLE
                        toolbar.visibility = View.GONE
                    }

                    else -> {
//                        hideToolBar()
//                        hideBottomBar()
                        bottomBar.visibility = View.GONE
                        toolbar.visibility = View.GONE
                    }
                }
            }

            profile.setOnClickListener {
                navController.navigate(
                    resId = R.id.profileFragment,
                    args = null,
                    navOptions = getNavOptions()
                )
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
                    lastId = navController.currentDestination?.id
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })

            bottomBar.startAnimation(showBottomBarAnimation)
        }
    }

    private fun showToolbar() {
        binding.apply {
            if (lastId == R.id.homeFragment ||
                lastId == R.id.announcementsFragment ||
                lastId == R.id.chatsListFragment
            ) return
            // Animation to show the toolbar
            val showToolbarAnimation = TranslateAnimation(
                0f, 0f, -toolbar.height.toFloat() - 7f.dpToPx(), 0f
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
            if (lastId == R.id.homeFragment ||
                lastId == R.id.announcementsFragment ||
                lastId == R.id.chatsListFragment
            ) return
            // Animation to hide the bottom navigation
            val hideBottomBarAnimation = TranslateAnimation(
                0f, 0f, 0f, bottomBar.height.toFloat() + 7f.dpToPx()
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
                lastId != R.id.announcementsFragment &&
                lastId != R.id.chatsListFragment &&
                lastId != R.id.profileFragment
            ) return
            // Animation to hide the toolbar
            val hideToolbarAnimation = TranslateAnimation(
                0f, 0f, 0f, -toolbar.height.toFloat() - 7f.dpToPx()
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

    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.getStartedFragment ||
            navController.currentDestination?.id == R.id.homeFragment ||
            navController.currentDestination?.id == R.id.loginFragment
        ) {
            finishAffinity()
        } else {
            when (navController.currentDestination?.id) {
                R.id.announcementsFragment,
                R.id.chatsListFragment,
                R.id.profileFragment -> {
                    navController.navigate(R.id.homeFragment)
                }
                R.id.noInternetFragment, R.id.selectJobLocationFragment -> {

                }

                else -> {
                    super.onBackPressed()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(connectivityReceiver)
        _binding = null
    }

    fun onNetworkChange(connected: Boolean) {
        if (connected) {
            if (navController.currentDestination?.id == R.id.noInternetFragment) {
                navController.popBackStack()
            }
        } else {
            if (navController.currentDestination?.id != R.id.noInternetFragment) {
                navController.navigate(R.id.noInternetFragment)
            }
        }
    }
}