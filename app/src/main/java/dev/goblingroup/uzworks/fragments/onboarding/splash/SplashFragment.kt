package dev.goblingroup.uzworks.fragments.onboarding.splash

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.database.AppDatabase
import dev.goblingroup.uzworks.databinding.FragmentSplashBinding
import dev.goblingroup.uzworks.models.response.LoginResponse
import dev.goblingroup.uzworks.networking.ApiClient
import dev.goblingroup.uzworks.utils.ApiStatus
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.NetworkHelper
import dev.goblingroup.uzworks.utils.UserRole
import dev.goblingroup.uzworks.utils.getNavOptions
import dev.goblingroup.uzworks.vm.LoginViewModel
import dev.goblingroup.uzworks.vm.LoginViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SplashFragment : Fragment(), CoroutineScope {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private var expandLogo: Animation? = null

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var loginViewModelFactory: LoginViewModelFactory
    private lateinit var networkHelper: NetworkHelper
    private lateinit var appDatabase: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            networkHelper = NetworkHelper(requireContext())
            appDatabase = AppDatabase.getInstance(requireContext())

            Log.d("TAG", "expand: testing room ${appDatabase.userDao().getUser()}")

            expandLogo = AnimationUtils.loadAnimation(requireContext(), R.anim.expand_logo)
            expand()
        }
    }

    private fun expand() {
        binding.logo.startAnimation(expandLogo)
        expandLogo?.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                login()
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

        })
    }

    private fun login() {
        loginViewModelFactory = LoginViewModelFactory(
            appDatabase = appDatabase,
            authService = ApiClient.authService,
            networkHelper = networkHelper,
            context = requireContext()
        )
        loginViewModel = ViewModelProvider(
            owner = this,
            factory = loginViewModelFactory
        )[LoginViewModel::class.java]
        launch {
            loginViewModel.login()
                .collect {
                    when (it) {
                        is ApiStatus.Error -> {
                            findNavController().navigate(
                                resId = R.id.getStartedFragment,
                                args = null,
                                navOptions = getNavOptions()
                            )
                            Log.e(TAG, "login: ${it.error.message}")
                            Log.e(TAG, "login: ${it.error.stackTrace}")
                            Log.e(TAG, "login: ${it.error.printStackTrace()}")
                        }

                        is ApiStatus.Loading -> {

                        }

                        is ApiStatus.Success -> {
                            findNavController().navigate(
                                resId = if ((it.response as LoginResponse).access.contains(UserRole.SUPER_ADMIN.roleName)) R.id.adminPanelFragment else R.id.homeFragment,
                                args = null
                            )
                        }
                    }
                }
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}