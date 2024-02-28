package dev.goblingroup.uzworks.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.adapter.rv_adapters.HomeAdapter
import dev.goblingroup.uzworks.databinding.FragmentHomeBinding
import dev.goblingroup.uzworks.utils.AnnouncementEnum
import dev.goblingroup.uzworks.utils.getNavOptions
import dev.goblingroup.uzworks.vm.AddressViewModel
import dev.goblingroup.uzworks.vm.AnnouncementViewModel
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.HomeViewModel
import dev.goblingroup.uzworks.vm.JobCategoryViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val TAG = "HomeFragment"

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()
    private val jobCategoryViewModel: JobCategoryViewModel by viewModels()

    private lateinit var linearSnapHelper: LinearSnapHelper

    private val announcementViewModel: AnnouncementViewModel by viewModels()
    private val addressViewModel: AddressViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            linearSnapHelper = LinearSnapHelper()
            lifecycleScope.launch {
                greetingTv.text = "${resources.getString(R.string.greeting)}\n${homeViewModel.getFullName()}"

                homeViewModel.workerCountLivedata.observe(viewLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {

                        }

                        is ApiStatus.Loading -> {

                        }

                        is ApiStatus.Success -> {
                            workersCount.text = it.response.toString()
                        }
                    }
                }
                homeViewModel.jobCountLiveData.observe(viewLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {

                        }

                        is ApiStatus.Loading -> {

                        }

                        is ApiStatus.Success -> {
                            jobsCount.text = it.response.toString()
                        }
                    }
                }
            }
            loadCategories()
        }
    }

    private fun loadCategories() {
        binding.apply {
            lifecycleScope.launch {
                Log.d(TAG, "loadCategories: started")
                jobCategoryViewModel.jobCategoriesLiveData.observe(viewLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {
                            Toast.makeText(requireContext(), "some error", Toast.LENGTH_SHORT)
                                .show()
                            Log.d(TAG, "loadCategories: failed")
                            Log.e(TAG, "loadCategories: ${it.error}")
                            Log.e(TAG, "loadCategories: ${it.error.printStackTrace()}")
                            Log.e(TAG, "loadCategories: ${it.error.stackTrace}")
                            Log.e(TAG, "loadCategories: ${it.error.message}")
                            progress.visibility = View.GONE
                            noAnnouncementsTv.visibility = View.VISIBLE
                        }

                        is ApiStatus.Loading -> {
                            Log.d(TAG, "loadCategories: loading")
                            progress.visibility = View.VISIBLE
                            noAnnouncementsTv.visibility = View.GONE
                        }

                        is ApiStatus.Success -> {
                            Log.d(TAG, "loadCategories: succeeded <${it.response?.size}>")
                            loadAddresses()
                        }

                        else -> {

                        }
                    }
                }
            }
        }
    }

    private fun loadAddresses() {
        binding.apply {
            lifecycleScope.launch {
                Log.d(TAG, "loadAddresses: started")
                addressViewModel.districtLiveData.observe(viewLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {
                            Toast.makeText(
                                requireContext(),
                                "some error while loading regions or districts",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e(TAG, "loadAddresses: failed")
                            noAnnouncementsTv.visibility = View.VISIBLE
                            progress.visibility = View.GONE
                        }
                        is ApiStatus.Loading -> {
                            Log.d(TAG, "loadAddresses: loading")
                            progress.visibility = View.VISIBLE
                            noAnnouncementsTv.visibility = View.GONE
                        }
                        is ApiStatus.Success -> {
                            Log.d(TAG, "loadAddresses: succeeded <${it.response?.size}>")
                            loadAnnouncements()
                        }
                    }
                }
            }
        }
    }

    private fun loadAnnouncements() {
        binding.apply {
            lifecycleScope.launch {
                Log.d(TAG, "loadAnnouncements: started")
                announcementViewModel.announcementLiveData.observe(viewLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {
                            Toast.makeText(requireContext(), "failed to load announcements", Toast.LENGTH_SHORT)
                                .show()
                            Log.d(TAG, "loadAnnouncements: failed")
                            Log.e(TAG, "loadAnnouncements: ${it.error}")
                            Log.e(TAG, "loadAnnouncements: ${it.error.printStackTrace()}")
                            Log.e(TAG, "loadAnnouncements: ${it.error.stackTrace}")
                            Log.e(TAG, "loadAnnouncements: ${it.error.message}")
                            progress.visibility = View.GONE
                            noAnnouncementsTv.visibility = View.VISIBLE
                        }

                        is ApiStatus.Loading -> {
                            Log.d(TAG, "loadAnnouncements: loading")
                            progress.visibility = View.VISIBLE
                            noAnnouncementsTv.visibility = View.GONE
                        }

                        is ApiStatus.Success -> {
                            Log.d(TAG, "loadAnnouncements: succeeded <${it.response?.size}>")
                            progress.visibility = View.GONE
                            success()
                        }

                        else -> {

                        }
                    }
                }
            }
        }
    }

    private fun success() {
        lifecycleScope.launch {
            if (_binding != null) {
                binding.apply {
                    binding.progress.visibility = View.GONE
                    val adapter = HomeAdapter(
                        announcementViewModel,
                        jobCategoryViewModel,
                        addressViewModel,
                        resources
                    ) { announcementId, announcementType ->
                        val bundle = Bundle()
                        bundle.putString("announcement_id", announcementId)
                        val directionId =
                            if (announcementType == AnnouncementEnum.JOB.announcementType) {
                                R.id.jobDetailsFragment
                            } else R.id.workerDetailsFragment
                        findNavController().navigate(
                            resId = directionId,
                            args = bundle,
                            navOptions = getNavOptions()
                        )
                    }
                    Log.d(TAG, "success: called in ${this@HomeFragment::class.java.simpleName}")
                    linearSnapHelper.attachToRecyclerView(recommendedWorkAnnouncementsRv)
                    recommendedWorkAnnouncementsRv.adapter = adapter
                    if (adapter.itemCount == 0) {
                        recommendedWorkAnnouncementsRv.visibility = View.INVISIBLE
                        noAnnouncementsTv.visibility = View.VISIBLE
                    } else {
                        recommendedWorkAnnouncementsRv.visibility = View.VISIBLE
                        noAnnouncementsTv.visibility = View.GONE
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