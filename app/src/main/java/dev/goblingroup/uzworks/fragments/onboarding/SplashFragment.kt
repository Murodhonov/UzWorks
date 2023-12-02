package dev.goblingroup.uzworks.fragments.onboarding

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentSplashBinding
import dev.goblingroup.uzworks.utils.getNavOptions

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private var expandLogo: Animation? = null

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

            expand()
        }
    }

    private fun expand() {
        binding.logo.startAnimation(expandLogo)
        expandLogo?.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                Handler().postDelayed({
                    lifecycleScope.launchWhenResumed {
                        findNavController().navigate(
                            resId = R.id.getStartedFragment,
                            args = null,
                            navOptions = getNavOptions()
                        )
                    }
                }, 1000)
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}