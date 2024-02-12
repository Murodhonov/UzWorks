package dev.goblingroup.uzworks.fragments.jobs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.adapter.rv_adapters.JobAdapter
import dev.goblingroup.uzworks.databinding.FragmentAllJobsBinding
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.JobCategoryViewModel
import dev.goblingroup.uzworks.vm.JobsViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllJobsFragment : Fragment() {

    private val TAG = "AllJobsFragment"

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
        Log.d(TAG, "onCreateView: lifecycle checking $TAG")
        _binding = FragmentAllJobsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            Log.d(TAG, "onViewCreated: lifecycle checking $TAG")
            loadJobs()
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
            lifecycleScope.launch {
                binding.apply {
                    jobAdapter = JobAdapter(
                        jobsViewModel.listDatabaseJobs(),
                        jobCategoryViewModel.listJobCategories(),
                        { jobId ->
                            jobClickListener?.onAllJobClick(jobId)
                        },
                        { state, jobId ->
                            saveUnSave(state, jobId)
                        }
                    )
                    recommendedWorkAnnouncementsRv.adapter = jobAdapter
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

    interface OnAllJobClickListener {
        fun onAllJobClick(jobId: String)
    }

    fun setOnJobClickListener(listener: OnAllJobClickListener) {
        jobClickListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: lifecycle checking $TAG")
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: lifecycle checking $TAG")
        loadJobs()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: lifecycle checking $TAG")
        loadJobs()
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            AllJobsFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}