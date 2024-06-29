package com.goblindevs.uzworks

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import com.goblindevs.uzworks.databinding.ActivityMainBinding
import com.goblindevs.uzworks.utils.LanguageManager
import com.goblindevs.uzworks.vm.MainViewModel
import com.goblindevs.uzworks.vm.SecurityViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

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

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(connectivityReceiver)
        _binding = null
    }

    fun onNetworkChange(connected: Boolean) {
        if (connected) {
            mainViewModel.connected()
        } else {
            mainViewModel.disconnected(this)
        }
        /*if (connected) {
            if (navController.currentDestination?.id == R.id.noInternetFragment) {
                navController.popBackStack()
            }
        } else {
            if (navController.currentDestination?.id != R.id.noInternetFragment) {
                navController.navigate(R.id.noInternetFragment)
            }
        }*/
    }
}