package dev.goblingroup.uzworks.fragments.main.jobs.job_list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dev.goblingroup.uzworks.adapters.rv_adapters.JobAdapter
import dev.goblingroup.uzworks.adapters.rv_adapters.SavedJobsAdapter
import dev.goblingroup.uzworks.database.AppDatabase
import dev.goblingroup.uzworks.databinding.FragmentJobListBinding
import dev.goblingroup.uzworks.networking.ApiClient
import dev.goblingroup.uzworks.singleton.MySharedPreference
import dev.goblingroup.uzworks.utils.ApiStatus
import dev.goblingroup.uzworks.utils.NetworkHelper
import dev.goblingroup.uzworks.vm.JobsViewModel
import dev.goblingroup.uzworks.vm.JobsViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class JobListFragment : Fragment(), CoroutineScope {

    private val TAG = "JobListFragment"

    private var _binding: FragmentJobListBinding? = null
    private val binding get() = _binding!!

    private var jobClickListener: OnJobClickListener? = null

    private lateinit var jobAdapter: JobAdapter
    private lateinit var savedJobsAdapter: SavedJobsAdapter

    private lateinit var jobsViewModel: JobsViewModel
    private lateinit var jobsViewModelFactory: JobsViewModelFactory

    private lateinit var appDatabase: AppDatabase
    private lateinit var networkHelper: NetworkHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJobListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            appDatabase = AppDatabase.getInstance(requireContext())
            networkHelper = NetworkHelper(requireContext())

            jobsViewModelFactory = JobsViewModelFactory(
                appDatabase = appDatabase,
                jobService = ApiClient.jobService,
                networkHelper = networkHelper,
                jobId = "",
                userId = MySharedPreference.getInstance(requireContext()).getUserId().toString()
            )
            jobsViewModel = ViewModelProvider(
                owner = this@JobListFragment,
                factory = jobsViewModelFactory
            )[JobsViewModel::class.java]

            val title = arguments?.getString("title")
            when (title) {
                "Barcha" -> {
                    Toast.makeText(requireContext(), "all", Toast.LENGTH_SHORT).show()
                    loadJobs()
                }

                "Saqlanganlar" -> {
                    Toast.makeText(requireContext(), "saved", Toast.LENGTH_SHORT).show()
                    loadSavedJobs()
                }
            }
        }
    }

    private fun loadJobs() {
        launch {
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
                            binding.progress.visibility = View.GONE
                        }

                        is ApiStatus.Success -> {
                            success()
                        }
                    }
                }
        }
    }

    private fun loadSavedJobs() {
        binding.apply {
            val savedJobList = jobsViewModel.listSavedJobs()
            if (savedJobList.isNotEmpty()) {
                emptyLayout.visibility = View.GONE
                savedJobsAdapter = SavedJobsAdapter(
                    jobsViewModel, {
                        jobClickListener?.onJobClick(it)
                    }, {
                        emptyLayout.visibility = View.VISIBLE
                    })
                recommendedWorkAnnouncementsRv.adapter = savedJobsAdapter
            } else {
                emptyLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun success() {
        binding.apply {
            jobAdapter = JobAdapter(jobsViewModel) {
                jobClickListener?.onJobClick(it)
            }
            recommendedWorkAnnouncementsRv.adapter = jobAdapter
        }
    }

    interface OnJobClickListener {
        fun onJobClick(position: Int)
    }

    fun setOnJobClickListener(listener: OnJobClickListener) {
        jobClickListener = listener
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    companion object {

        @JvmStatic
        fun newInstance(title: String) =
            JobListFragment().apply {
                arguments = Bundle().apply {
                    putString("title", title)
                }
            }
    }
}