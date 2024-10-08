package com.goblindevs.uzworks.fragments.onboarding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.goblindevs.uzworks.R
import com.goblindevs.uzworks.databinding.FragmentSplashBinding
import com.goblindevs.uzworks.utils.ConstValues.TAG
import com.goblindevs.uzworks.utils.LanguageManager
import com.goblindevs.uzworks.vm.ApiStatus
import com.goblindevs.uzworks.vm.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private var expandLogo: Animation? = null

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            expandLogo = AnimationUtils.loadAnimation(requireContext(), R.anim.expand_logo)

            logo.startAnimation(expandLogo)
            expandLogo?.setAnimationListener(object : AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    if (splashViewModel.getLanguageCode() == null) {
                        splashViewModel.setLanguageCode("uz")
                        LanguageManager.setLocale("uz", requireContext())
                    } else {
                        LanguageManager.setLocale(splashViewModel.getLanguageCode().toString(), requireContext())
                    }
                    login()
                }

                override fun onAnimationRepeat(animation: Animation?) {

                }

            })
        }
    }

    private fun login() {
        lifecycleScope.launch {
            splashViewModel.splashLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        findNavController().navigate(
                            resId = R.id.action_splashFragment_to_getStartedFragment,
                            args = null
                        )
                    }

                    is ApiStatus.Loading -> {

                    }

                    is ApiStatus.Success -> {
                        if (it.response == true) {
                            findNavController().navigate(
                                resId = R.id.action_splashFragment_to_startFragment,
                                args = null
                            )
                        } else {
                            findNavController().navigate(
                                resId = R.id.action_splashFragment_to_getStartedFragment,
                                args = null
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}