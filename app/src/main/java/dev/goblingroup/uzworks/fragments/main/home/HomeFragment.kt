package dev.goblingroup.uzworks.fragments.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.adapters.rv_adapters.JobAdapter
import dev.goblingroup.uzworks.database.AppDatabase
import dev.goblingroup.uzworks.databinding.FragmentHomeBinding
import dev.goblingroup.uzworks.networking.ApiClient
import dev.goblingroup.uzworks.singleton.MySharedPreference
import dev.goblingroup.uzworks.utils.ApiStatus
import dev.goblingroup.uzworks.utils.NetworkHelper
import dev.goblingroup.uzworks.utils.getNavOptions
import dev.goblingroup.uzworks.vm.JobCategoryViewModel
import dev.goblingroup.uzworks.vm.JobCategoryViewModelFactory
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

    private lateinit var jobsViewModel: JobsViewModel
    private lateinit var jobsViewModelFactory: JobsViewModelFactory

    private lateinit var linearSnapHelper: LinearSnapHelper

    private lateinit var jobCategoryViewModel: JobCategoryViewModel
    private lateinit var jobCategoryViewModelFactory: JobCategoryViewModelFactory

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
            linearSnapHelper = LinearSnapHelper()
            val user = appDatabase.userDao().getUser()

            greetingTv.text = "Assalomu alaykum\n${user?.firstname} ${user?.lastName}"
            jobsViewModelFactory = JobsViewModelFactory(
                appDatabase = appDatabase,
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
                            binding.progress.visibility = View.VISIBLE
                        }

                        is ApiStatus.Success -> {
                            success()
                        }
                    }
                }
        }
    }

    private fun success() {
        if (_binding != null) {
            binding.apply {
                jobCategoryViewModelFactory = JobCategoryViewModelFactory(
                    appDatabase,
                    ApiClient.jobCategoryService,
                    networkHelper
                )
                jobCategoryViewModel = ViewModelProvider(
                    owner = this@HomeFragment,
                    factory = jobCategoryViewModelFactory
                )[JobCategoryViewModel::class.java]

                binding.progress.visibility = View.GONE
                val adapter = JobAdapter(jobsViewModel, jobCategoryViewModel) {
                    findNavController().navigate(
                        resId = R.id.jobDetailsFragment,
                        args = null,
                        navOptions = getNavOptions()
                    )
                }
                linearSnapHelper.attachToRecyclerView(recommendedWorkAnnouncementsRv)
                recommendedWorkAnnouncementsRv.adapter = adapter
            }
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}