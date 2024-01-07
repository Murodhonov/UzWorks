package dev.goblingroup.uzworks.fragments.main.jobs.job_list

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dev.goblingroup.uzworks.adapters.rv_adapters.SavedJobsAdapter
import dev.goblingroup.uzworks.database.AppDatabase
import dev.goblingroup.uzworks.databinding.FragmentSavedJobsBinding
import dev.goblingroup.uzworks.networking.ApiClient
import dev.goblingroup.uzworks.singleton.MySharedPreference
import dev.goblingroup.uzworks.utils.NetworkHelper
import dev.goblingroup.uzworks.vm.JobCategoryViewModel
import dev.goblingroup.uzworks.vm.JobCategoryViewModelFactory
import dev.goblingroup.uzworks.vm.JobsViewModel
import dev.goblingroup.uzworks.vm.JobsViewModelFactory

class SavedJobsFragment : Fragment() {

    private val TAG = "SavedJobsFragment"

    private var _binding: FragmentSavedJobsBinding? = null
    private val binding get() = _binding!!

    private var jobClickListener: OnSavedJobClickListener? = null
    private var findJobClickListener: OnFindJobClickListener? = null

    private lateinit var savedJobsAdapter: SavedJobsAdapter

    private lateinit var jobsViewModel: JobsViewModel
    private lateinit var jobsViewModelFactory: JobsViewModelFactory

    private lateinit var appDatabase: AppDatabase
    private lateinit var networkHelper: NetworkHelper

    private lateinit var jobCategoryViewModel: JobCategoryViewModel
    private lateinit var jobCategoryViewModelFactory: JobCategoryViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: lifecycle checking")
        _binding = FragmentSavedJobsBinding.inflate(layoutInflater)
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
            loadSavedJobs()
            findJobBtn.setOnClickListener {
                findJobClickListener?.onFindJobClick()
            }
        }
    }

    private fun loadSavedJobs() {
        binding.apply {
            val savedJobList = jobsViewModel.listSavedJobs()
            if (savedJobList.isNotEmpty()) {
                jobCategoryViewModelFactory = JobCategoryViewModelFactory(
                    appDatabase,
                    ApiClient.jobCategoryService,
                    networkHelper
                )
                jobCategoryViewModel = ViewModelProvider(
                    owner = requireActivity(),
                    factory = jobCategoryViewModelFactory
                )[JobCategoryViewModel::class.java]

                emptyLayout.visibility = View.GONE
                savedJobsAdapter = SavedJobsAdapter(
                    jobsViewModel,
                    jobCategoryViewModel,
                    { clickedJobId ->
                        jobClickListener?.onSavedJobClick(clickedJobId)
                    },
                    {
                        emptyLayout.visibility = View.VISIBLE
                    }
                )
                recommendedWorkAnnouncementsRv.adapter = savedJobsAdapter
            } else {
                emptyLayout.visibility = View.VISIBLE
            }
        }
    }

    interface OnSavedJobClickListener {
        fun onSavedJobClick(jobId: String)
    }

    interface OnFindJobClickListener {
        fun onFindJobClick()
    }

    fun setOnFindJobClickListener(listener: OnFindJobClickListener) {
        findJobClickListener = listener
    }

    fun setOnJobClickListener(listener: OnSavedJobClickListener) {
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
        loadSavedJobs()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: lifecycle checking")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: lifecycle checking")
        loadSavedJobs()
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: lifecycle checking")
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SavedJobsFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}