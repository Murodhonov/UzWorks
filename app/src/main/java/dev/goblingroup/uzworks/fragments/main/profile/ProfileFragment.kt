package dev.goblingroup.uzworks.fragments.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentProfileBinding
import dev.goblingroup.uzworks.utils.aboutDialog
import dev.goblingroup.uzworks.utils.experienceDialog
import dev.goblingroup.uzworks.utils.fieldsDialog
import dev.goblingroup.uzworks.utils.getNavOptions
import dev.goblingroup.uzworks.utils.personalInfoDialog

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            settings.setOnClickListener {
                findNavController().navigate(
                    resId = R.id.settingsFragment,
                    args = null,
                    navOptions = getNavOptions()
                )
            }

            personalInfoBtn.setOnClickListener {
                personalInfoDialog(requireContext(), layoutInflater)
            }

            aboutBtn.setOnClickListener {
                aboutDialog(requireContext(), layoutInflater)
            }

            fieldsBtn.setOnClickListener {
                fieldsDialog(requireContext(), layoutInflater, resources)
            }

            experienceBtn.setOnClickListener {
                experienceDialog(requireContext(), layoutInflater)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}