package dev.goblingroup.uzworks.fragments.jobs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.adapter.rv_adapters.SavedJobsAdapter
import dev.goblingroup.uzworks.database.entity.JobEntity
import dev.goblingroup.uzworks.databinding.FragmentSavedJobsBinding
import dev.goblingroup.uzworks.vm.JobCategoryViewModel
import dev.goblingroup.uzworks.vm.JobsViewModel
import kotlinx.coroutines.launch

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

    private lateinit var savedJobs: ArrayList<JobEntity>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: lifecycle checking $TAG")
        _binding = FragmentSavedJobsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            Log.d(TAG, "onViewCreated: lifecycle checking $TAG")
            loadSavedJobs()
            findJobBtn.setOnClickListener {
                findJobClickListener?.onFindJobClick()
            }
        }
    }

    private fun loadSavedJobs() {
        lifecycleScope.launch {
            binding.apply {
                savedJobs = ArrayList(jobsViewModel.listSavedJobs())
                if (savedJobs.isNotEmpty()) {
                    emptyLayout.visibility = View.GONE
                    savedJobsAdapter = SavedJobsAdapter(
                        savedJobs,
                        jobCategoryViewModel.listJobCategories(),
                        { clickedJobId ->
                            jobClickListener?.onSavedJobClick(clickedJobId)
                        },
                        { jobId, position ->
                            unSave(jobId, position)
                        }
                    )
                    recommendedWorkAnnouncementsRv.adapter = savedJobsAdapter
                } else {
                    emptyLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun unSave(jobId: String, position: Int) {
        lifecycleScope.launch {
            binding.apply {
                savedJobs.removeAt(position)
                savedJobsAdapter.notifyItemRemoved(position)
                savedJobsAdapter.notifyItemRangeChanged(
                    position,
                    jobsViewModel.countSavedJobs() - position
                )
                if (!jobsViewModel.unSaveJob(jobId)) {
                    emptyLayout.visibility = View.VISIBLE
                }
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
        Log.d(TAG, "onDestroyView: lifecycle checking $TAG")
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: lifecycle checking $TAG")
        loadSavedJobs()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: lifecycle checking $TAG")
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