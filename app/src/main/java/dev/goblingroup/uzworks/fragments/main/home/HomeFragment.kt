package dev.goblingroup.uzworks.fragments.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.adapters.rv_adapters.WorksAdapter
import dev.goblingroup.uzworks.databinding.FragmentHomeBinding
import dev.goblingroup.uzworks.utils.getNavOptions

class HomeFragment : Fragment() {

    private val TAG = "HomeFragment"

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            val adapter = WorksAdapter {
                findNavController().navigate(
                    resId = R.id.jobDetailsFragment,
                    args = null,
                    navOptions = getNavOptions()
                )
            }
            recommendedWorkAnnouncementsRv.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}