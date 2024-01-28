package dev.goblingroup.uzworks.fragments.jobs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.adapters.rv_adapters.SavedJobsAdapter
import dev.goblingroup.uzworks.databinding.FragmentSavedJobsBinding
import dev.goblingroup.uzworks.vm.JobCategoryViewModel
import dev.goblingroup.uzworks.vm.JobsViewModel

@AndroidEntryPoint
class SavedJobsFragment : Fragment() {

    private val TAG = "SavedJobsFragment"

    private var _binding: FragmentSavedJobsBinding? = null
    private val binding get() = _binding!!

    private var jobClickListener: OnSavedJobClickListener? = null
    private var findJobClickListener: OnFindJobClickListener? = null

    private lateinit var savedJobsAdapter: SavedJobsAdapter

    private val jobsViewModel: JobsViewModel by viewModels()
    private val jobCategoryViewModel: JobCategoryViewModel by viewModels()

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

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: lifecycle checking")
        loadSavedJobs()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: lifecycle checking")
        loadSavedJobs()
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