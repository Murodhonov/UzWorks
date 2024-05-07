package dev.goblingroup.uzworks.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.adapter.HomeFragmentAdapter
import dev.goblingroup.uzworks.databinding.FragmentHomeBinding
import dev.goblingroup.uzworks.utils.AnnouncementEnum
import dev.goblingroup.uzworks.vm.HomeViewModel

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val TAG = "HomeFragment"

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            val homeFragmentAdapter = HomeFragmentAdapter(
                viewLifecycleOwner,
                homeViewModel,
                resources
            ) { announcementId, announcementType ->
                announcementDetails(announcementId, announcementType)
            }
            homeRv.adapter = homeFragmentAdapter

            homeViewModel.allResponseReceived.observe(viewLifecycleOwner) {
                if (it) {
                    swipeRefresh.isRefreshing = false
                }
            }

            swipeRefresh.setOnRefreshListener {
                homeViewModel.fetchData()
            }
        }
    }

    private fun announcementDetails(announcementId: String, announcementType: String) {
        val bundle = Bundle()
        bundle.putString("announcement_id", announcementId)
        Log.d(TAG, "announcementDetails: bundle $bundle announcementType -> $announcementType")
        if (announcementType == AnnouncementEnum.JOB.announcementType) {
            Log.d(TAG, "announcementDetails: navigating to job details")
            findNavController().navigate(
                resId = R.id.action_startFragment_to_jobDetailsFragment,
                args = bundle
            )
        } else {
            Log.d(TAG, "announcementDetails: navigating to worker details")
            findNavController().navigate(
                resId = R.id.action_startFragment_to_workerDetailsFragment,
                args = bundle
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

}