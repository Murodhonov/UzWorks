package dev.goblingroup.uzworks.fragments.jobs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.adapters.rv_adapters.JobAdapter
import dev.goblingroup.uzworks.databinding.FragmentAllJobsBinding
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.JobCategoryViewModel
import dev.goblingroup.uzworks.vm.JobsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class AllJobsFragment : Fragment(), CoroutineScope {

    private val TAG = "JobListFragment"

    private var _binding: FragmentAllJobsBinding? = null
    private val binding get() = _binding!!

    private var jobClickListener: OnAllJobClickListener? = null

    private lateinit var jobAdapter: JobAdapter

    private val jobsViewModel: JobsViewModel by viewModels()
    private val jobCategoryViewModel: JobCategoryViewModel by viewModels()

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
            loadJobs()
        }
    }

    private fun loadJobs() {
        launch {
            jobsViewModel.jobsStateFlow
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
                            Log.d(TAG, "loadJobs: ${it.response}")
                            success()
                        }
                    }
                }
        }
    }

    private fun success() {
        if (_binding != null) {
            binding.apply {
                jobAdapter = JobAdapter(jobsViewModel, jobCategoryViewModel) { clickedJobId ->
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

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: lifecycle checking")
        loadJobs()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: lifecycle checking")
        loadJobs()
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    companion object {

        @JvmStatic
        fun newInstance() =
            AllJobsFragment().apply {
                arguments = Bundle().apply {
//                    putString("title", title)
                }
            }
    }
}