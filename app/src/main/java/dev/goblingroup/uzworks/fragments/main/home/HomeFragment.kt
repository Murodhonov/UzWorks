package dev.goblingroup.uzworks.fragments.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.adapters.rv_adapters.JobAdapter
import dev.goblingroup.uzworks.database.AppDatabase
import dev.goblingroup.uzworks.databinding.FragmentHomeBinding
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.networking.ApiClient
import dev.goblingroup.uzworks.singleton.MySharedPreference
import dev.goblingroup.uzworks.utils.ApiStatus
import dev.goblingroup.uzworks.utils.NetworkHelper
import dev.goblingroup.uzworks.utils.getNavOptions
import dev.goblingroup.uzworks.vm.JobsViewModel
import dev.goblingroup.uzworks.vm.JobsViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class HomeFragment : Fragment(), CoroutineScope {

    private val TAG = "HomeFragment"

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var appDatabase: AppDatabase
    private lateinit var networkHelper: NetworkHelper

    private lateinit var jobsViewModel: JobsViewModel by viewModels()
    private lateinit var jobsViewModelFactory: JobsViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            appDatabase = AppDatabase.getInstance(requireContext())
            networkHelper = NetworkHelper(requireContext())
            val user = appDatabase.userDao().getUser()

            greetingTv.text = "Assalomu alaykum\n${user?.firstname} ${user?.lastName}"
            jobsViewModelFactory = JobsViewModelFactory(
                jobService = ApiClient.jobService,
                networkHelper = networkHelper,
                jobId = "",
                userId = MySharedPreference.getInstance(requireContext()).getUserId().toString()
            )
            loadJobs()
        }
    }

    private fun loadJobs() {
        launch {
            jobsViewModel = ViewModelProvider(
                owner = this@HomeFragment,
                factory = jobsViewModelFactory
            )[JobsViewModel::class.java]
            jobsViewModel.getAllJobs()
                .collect {
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
                            Toast.makeText(requireContext(), "loading...", Toast.LENGTH_SHORT)
                                .show()
                        }

                        is ApiStatus.Success -> {
                            success(it.response as List<JobResponse>)
                        }
                    }
                }
        }
    }

    private fun success(jobList: List<JobResponse>) {
        binding.apply {
            val adapter = JobAdapter(jobList) {
                findNavController().navigate(
                    resId = R.id.jobDetailsFragment,
                    args = null,
                    navOptions = getNavOptions()
                )
            }
            LinearSnapHelper().attachToRecyclerView(recommendedWorkAnnouncementsRv)
            recommendedWorkAnnouncementsRv.adapter = adapter
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}