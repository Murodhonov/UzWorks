package dev.goblingroup.uzworks.fragments.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentSplashBinding
import dev.goblingroup.uzworks.vm.AddressViewModel
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.JobCategoryViewModel
import dev.goblingroup.uzworks.vm.SplashViewModel
import kotlinx.coroutines.launch

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