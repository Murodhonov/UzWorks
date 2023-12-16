package dev.goblingroup.uzworks.fragments.main.jobs.job_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentJobDetailsBinding
import dev.goblingroup.uzworks.utils.getNavOptions

class JobDetailsFragment : Fragment() {

    private val TAG = "JobDetailsFragment"

    private var _binding: FragmentJobDetailsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJobDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            contactChatBtn.setOnClickListener {
                findNavController().navigate(
                    resId = R.id.chatFragment,
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