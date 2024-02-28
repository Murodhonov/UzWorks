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
import androidx.recyclerview.widget.LinearSnapHelper
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.adapter.rv_adapters.AnnouncementsAdapter
import dev.goblingroup.uzworks.databinding.FragmentHomeBinding
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
                    }

                    is ApiStatus.Loading -> {
                        Log.d(TAG, "loadCategories: loading")
                        binding.progress.visibility = View.VISIBLE
                        binding.noAnnouncementsTv.visibility = View.GONE
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

    private fun loadAddresses() {
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
                        binding.progress.visibility = View.GONE
                        Log.e(TAG, "loadAddresses: failed")
                    }
                    is ApiStatus.Loading -> {
                        Log.d(TAG, "loadAddresses: loading")
                    }
                    is ApiStatus.Success -> {
                        Log.d(TAG, "loadAddresses: succeeded <${it.response?.size}>")
                        loadAnnouncements()
                    }
                }
            }
        }
    }

    private fun loadAnnouncements() {
        lifecycleScope.launch {
            Log.d(TAG, "loadAnnouncements: started")
            announcementViewModel.combinedLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        Toast.makeText(requireContext(), "some error", Toast.LENGTH_SHORT)
                            .show()
                        Log.d(TAG, "loadAnnouncements: failed")
                        Log.e(TAG, "loadAnnouncements: ${it.error}")
                        Log.e(TAG, "loadAnnouncements: ${it.error.printStackTrace()}")
                        Log.e(TAG, "loadAnnouncements: ${it.error.stackTrace}")
                        Log.e(TAG, "loadAnnouncements: ${it.error.message}")
                    }

                    is ApiStatus.Loading -> {
                        Log.d(TAG, "loadAnnouncements: loading")
                    }

                    is ApiStatus.Success -> {
                        Log.d(TAG, "loadAnnouncements: succeeded jobs <${it.response?.jobs?.size}>\tworkers <${it.response?.workers?.size}>")
                        success()
                    }

                    else -> {

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
                    Log.d(
                        TAG,
                        "success: jobs ${announcementViewModel.listDatabaseAnnouncements().jobs?.size}"
                    )
                    Log.d(
                        TAG,
                        "success: workers ${announcementViewModel.listDatabaseAnnouncements().workers?.size}"
                    )
                    val adapter = AnnouncementsAdapter(
                        announcementViewModel.listDatabaseAnnouncements(),
                        jobCategoryViewModel.listJobCategories(),
                        addressViewModel = addressViewModel,
                        { announcementId ->

                        }, { state, announcementId, position ->
                            saveUnSave(state, announcementId)
                        }
                    )
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

    private fun saveUnSave(state: Boolean, announcementId: String) {
        lifecycleScope.launch {
            if (state) {
                announcementViewModel.saveAnnouncement(announcementId)
            } else {
                announcementViewModel.unSaveAnnouncement(announcementId)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}