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
import dev.goblingroup.uzworks.adapter.rv_adapters.JobAdapter
import dev.goblingroup.uzworks.databinding.FragmentHomeBinding
import dev.goblingroup.uzworks.utils.getNavOptions
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.JobCategoryViewModel
import dev.goblingroup.uzworks.vm.JobsViewModel
import dev.goblingroup.uzworks.vm.LoginViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val TAG = "HomeFragment"

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val jobsViewModel: JobsViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private val jobCategoryViewModel: JobCategoryViewModel by viewModels()

    private lateinit var linearSnapHelper: LinearSnapHelper

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
                val user = loginViewModel.getUser()
                greetingTv.text = "Assalomu alaykum\n${user?.firstname} ${user?.lastName}"
            }

            loadCategories()
        }
    }

    private fun loadCategories() {
        lifecycleScope.launch {
            jobCategoryViewModel.jobCategoriesLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        Toast.makeText(requireContext(), "some error", Toast.LENGTH_SHORT)
                            .show()
                        Log.e(TAG, "loadCategories: ${it.error}")
                        Log.e(TAG, "loadCategories: ${it.error.printStackTrace()}")
                        Log.e(TAG, "loadCategories: ${it.error.stackTrace}")
                        Log.e(TAG, "loadCategories: ${it.error.message}")
                    }

                    is ApiStatus.Loading -> {
                        binding.progress.visibility = View.VISIBLE
                    }

                    is ApiStatus.Success -> {
                        loadJobs()
                    }
                }
            }
        }
    }

    private fun loadJobs() {
        lifecycleScope.launch {
            jobsViewModel.jobsLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        Toast.makeText(requireContext(), "some error", Toast.LENGTH_SHORT)
                            .show()
                        Log.e(TAG, "loadJobs: ${it.error}")
                        Log.e(TAG, "loadJobs: ${it.error.printStackTrace()}")
                        Log.e(TAG, "loadJobs: ${it.error.stackTrace}")
                        Log.e(TAG, "loadJobs: ${it.error.message}")
                    }

                    is ApiStatus.Loading -> {

                    }

                    is ApiStatus.Success -> {
                        success()
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
                    val adapter = JobAdapter(
                        jobsViewModel.listDatabaseJobs(),
                        jobCategoryViewModel.listJobCategories(),
                        { jobId ->
                            findNavController().navigate(
                                resId = R.id.jobDetailsFragment,
                                args = null,
                                navOptions = getNavOptions()
                            )
                        },
                        { state, jobId ->
                            saveUnSave(state, jobId)
                        }
                    )
                    linearSnapHelper.attachToRecyclerView(recommendedWorkAnnouncementsRv)
                    recommendedWorkAnnouncementsRv.adapter = adapter
                }
            }
        }
    }

    private fun saveUnSave(state: Boolean, jobId: String) {
        lifecycleScope.launch {
            if (state) {
                jobsViewModel.saveJob(jobId)
            } else {
                jobsViewModel.unSaveJob(jobId)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}