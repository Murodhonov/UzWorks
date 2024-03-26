package dev.goblingroup.uzworks.fragments.announcement

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.adapter.MyJobsAdapter
import dev.goblingroup.uzworks.databinding.FragmentMyAnnouncementsBinding
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.models.response.WorkerResponse
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.getNavOptions
import dev.goblingroup.uzworks.vm.AddressViewModel
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.JobCategoryViewModel
import dev.goblingroup.uzworks.vm.MyAnnouncementsViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyAnnouncementsFragment : Fragment() {

    private var _binding: FragmentMyAnnouncementsBinding? = null
    private val binding get() = _binding!!

    private val myAnnouncementsViewModel: MyAnnouncementsViewModel by viewModels()
    private val jobCategoryViewModel: JobCategoryViewModel by viewModels()
    private val addressViewModel: AddressViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyAnnouncementsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            loadAnnouncements()
        }
    }

    private fun loadAnnouncements() {
        if (myAnnouncementsViewModel.isEmployee()) {
            loadWorkers()
        } else {
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
                jobCategoryList = jobCategoryViewModel.listJobCategories(),
                addressViewModel = addressViewModel,
                { jobId ->
                    val bundle = Bundle()
                    bundle.putString("job_id", jobId)
                    findNavController().navigate(
                        resId = R.id.jobDetailsFragment,
                        args = bundle,
                        navOptions = getNavOptions()
                    )
                }, { jobId ->
                    showBottomDialog(jobId)
                }
            )
            myAnnouncementsRv.adapter = myJobsAdapter
            if (myJobsAdapter.itemCount == 0) {
                noAnnouncementsTv.visibility = View.VISIBLE
            } else {
                noAnnouncementsTv.visibility = View.GONE
            }
        }
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

    }

    private fun showBottomDialog(announcementId: String) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}