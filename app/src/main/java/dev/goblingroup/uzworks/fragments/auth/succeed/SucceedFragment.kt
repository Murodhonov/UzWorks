package dev.goblingroup.uzworks.fragments.auth.succeed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentSucceedBinding
import dev.goblingroup.uzworks.utils.getNavOptions

class SucceedFragment : Fragment() {

    private var _binding: FragmentSucceedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSucceedBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            continueBtn.setOnClickListener {
                findNavController().navigate(
                    resId = R.id.homeFragment,
                    args = null,
                    navOptions = getNavOptions()
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}