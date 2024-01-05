package dev.goblingroup.uzworks.fragments.main.jobs.job_list

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dev.goblingroup.uzworks.adapters.rv_adapters.JobAdapter
import dev.goblingroup.uzworks.database.AppDatabase
import dev.goblingroup.uzworks.databinding.FragmentAllJobsBinding
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

class AllJobsFragment : Fragment(), CoroutineScope {

    private val TAG = "JobListFragment"

    private var _binding: FragmentAllJobsBinding? = null
    private val binding get() = _binding!!

    private var jobClickListener: OnAllJobClickListener? = null

    private lateinit var jobAdapter: JobAdapter

    private lateinit var jobsViewModel: JobsViewModel
    private lateinit var jobsViewModelFactory: JobsViewModelFactory

    private lateinit var appDatabase: AppDatabase
    private lateinit var networkHelper: NetworkHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: lifecycle checking")
        _binding = FragmentAllJobsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            Log.d(TAG, "onViewCreated: lifecycle checking")
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
                owner = requireActivity(),
                factory = jobsViewModelFactory
            )[JobsViewModel::class.java]
            loadJobs()
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

    private fun success() {
        if (_binding != null) {
            binding.apply {
                jobAdapter = JobAdapter(jobsViewModel) { clickedJobId ->
                    jobClickListener?.onAllJobClick(clickedJobId)
                }
                recommendedWorkAnnouncementsRv.adapter = jobAdapter
            }
        }
    }

    interface OnAllJobClickListener {
        fun onAllJobClick(jobId: String)
    }

    fun setOnJobClickListener(listener: OnAllJobClickListener) {
        jobClickListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: lifecycle checking")
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach: lifecycle checking")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach: lifecycle checking")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: lifecycle checking")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: lifecycle checking")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: lifecycle checking")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: lifecycle checking")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: lifecycle checking")
        loadJobs()
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: lifecycle checking")
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    companion object {

        @JvmStatic
        fun newInstance(title: String) =
            AllJobsFragment().apply {
                arguments = Bundle().apply {
                    putString("title", title)
                }
            }
    }
}