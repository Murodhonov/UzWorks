package dev.goblingroup.uzworks.fragments.announcement

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.adapter.MyJobsAdapter
import dev.goblingroup.uzworks.adapter.MyWorkersAdapter
import dev.goblingroup.uzworks.databinding.FragmentMyAnnouncementsBinding
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.models.response.WorkerResponse
import dev.goblingroup.uzworks.utils.AnnouncementEnum
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.MyAnnouncementsViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyAnnouncementsFragment : Fragment() {

    private var _binding: FragmentMyAnnouncementsBinding? = null
    private val binding get() = _binding!!

    private val myAnnouncementsViewModel: MyAnnouncementsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyAnnouncementsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            back.setOnClickListener {
                findNavController().popBackStack()
            }
            loadAnnouncements()
        }
    }

    private fun loadAnnouncements() {
        if (myAnnouncementsViewModel.isEmployee()) {
            loadWorkers()
        } else if (myAnnouncementsViewModel.isEmployer()) {
            loadJobs()
        }
    }

    private fun loadJobs() {
        lifecycleScope.launch {
            myAnnouncementsViewModel.jobLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        Toast.makeText(requireContext(), "failed to fetch jobs", Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE
                    }

                    is ApiStatus.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is ApiStatus.Success -> {
                        Log.d(TAG, "loadJobs: succeeded ${it.response}")
                        binding.progressBar.visibility = View.GONE
                        setJobs(it.response!!)
                    }
                }
            }
        }
    }

    private fun setJobs(jobList: List<JobResponse>) {
        binding.apply {
            val myJobsAdapter = MyJobsAdapter(
                jobList = jobList,
                resources = resources,
            ) { jobId ->
                showBottom(jobId, AnnouncementEnum.JOB.announcementType)
            }
            myAnnouncementsRv.adapter = myJobsAdapter
            if (myJobsAdapter.itemCount == 0) {
                noAnnouncementsTv.visibility = View.VISIBLE
            } else {
                noAnnouncementsTv.visibility = View.GONE
            }
        }
    }

    private fun showBottom(jobId: String, announcementType: String) {
        val bundle = Bundle()
        bundle.putString("announcement_id", jobId)
        myAnnouncementsViewModel.showBottom(
            requireContext(),
            {
                val direction = when (announcementType) {
                    AnnouncementEnum.JOB.announcementType -> {
                        R.id.action_myAnnouncementsFragment_to_jobDetailsFragment
                    }

                    AnnouncementEnum.WORKER.announcementType -> {
                        R.id.action_myAnnouncementsFragment_to_workerDetailsFragment
                    }

                    else -> 0
                }
                findNavController().navigate(resId = direction, args = bundle)
            },
            {
                val direction = when (announcementType) {
                    AnnouncementEnum.JOB.announcementType -> {
                        R.id.action_myAnnouncementsFragment_to_addJobFragment
                    }

                    AnnouncementEnum.WORKER.announcementType -> {
                        R.id.action_myAnnouncementsFragment_to_addWorkerFragment
                    }

                    else -> {
                        0
                    }
                }
                findNavController().navigate(resId = direction, args = bundle)
            }, {

            }
        )
    }

    private fun loadWorkers() {
        lifecycleScope.launch {
            myAnnouncementsViewModel.workerLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        Toast.makeText(requireContext(), "failed to fetch workers", Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE
                    }
                    is ApiStatus.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is ApiStatus.Success -> {
                        Log.d(TAG, "loadWorkers: succeeded ${it.response}")
                        binding.progressBar.visibility = View.GONE
                        setWorkers(it.response!!)
                    }
                }
            }
        }
    }

    private fun setWorkers(workerList: List<WorkerResponse>) {
        binding.apply {
            val myJobsAdapter = MyWorkersAdapter(
                jobList = workerList,
                resources = resources,
            ) { jobId ->
                showBottom(jobId, AnnouncementEnum.WORKER.announcementType)
            }
            myAnnouncementsRv.adapter = myJobsAdapter
            if (myJobsAdapter.itemCount == 0) {
                noAnnouncementsTv.visibility = View.VISIBLE
            } else {
                noAnnouncementsTv.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}