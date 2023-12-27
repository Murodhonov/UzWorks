package dev.goblingroup.uzworks.fragments.main.jobs.job_list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.goblingroup.uzworks.adapters.rv_adapters.JobAdapter
import dev.goblingroup.uzworks.databinding.FragmentJobListBinding
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.networking.ApiClient
import dev.goblingroup.uzworks.pagination.PaginationScrollListener
import dev.goblingroup.uzworks.resource.JobResource
import dev.goblingroup.uzworks.utils.ApiStatus
import dev.goblingroup.uzworks.utils.NetworkHelper
import dev.goblingroup.uzworks.vm.JobsViewModel
import dev.goblingroup.uzworks.vm.JobsViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.math.ceil

class JobListFragment : Fragment(), CoroutineScope {

    private val TAG = "JobListFragment"

    private var _binding: FragmentJobListBinding? = null
    private val binding get() = _binding!!

    private var jobClickListener: OnJobClickListener? = null

    private lateinit var jobAdapter: JobAdapter

    private lateinit var jobsViewModel: JobsViewModel
    private lateinit var jobsViewModelFactory: JobsViewModelFactory
    private lateinit var networkHelper: NetworkHelper

    private var currentPage = 1
    private var TOTAL_PAGES = 0
    private var isLastPage = false
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJobListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            val title = arguments?.getString("title")
            when (title) {
                "Barcha" -> {
                    emptyLayout.visibility = View.GONE
                    recommendedWorkAnnouncementsRv.visibility = View.VISIBLE
                }

                "Saqlanganlar" -> {
                    emptyLayout.visibility = View.VISIBLE
                    recommendedWorkAnnouncementsRv.visibility = View.GONE
                }
            }

            networkHelper = NetworkHelper(requireContext())
            jobsViewModelFactory = JobsViewModelFactory(
                jobService = ApiClient.jobService,
                networkHelper = networkHelper,
                jobId = "",
                userId = ""
            )
            jobsViewModel = ViewModelProvider(
                owner = this@JobListFragment,
                factory = jobsViewModelFactory
            )[JobsViewModel::class.java]

            val linearLayoutManager = LinearLayoutManager(requireContext())
            recommendedWorkAnnouncementsRv.layoutManager = linearLayoutManager
            jobAdapter = JobAdapter { position ->
                /**
                 * item clicked
                 */
            }
            recommendedWorkAnnouncementsRv.adapter = jobAdapter

            loadFirstPage()

            recommendedWorkAnnouncementsRv.addOnScrollListener(object :
                PaginationScrollListener(linearLayoutManager) {
                override fun loadMoreItems() {
                    currentPage++
                    isLoading = true
                    loadNextPage()
                }

                override fun isLastPage(): Boolean {
                    return isLastPage
                }

                override fun isLoading(): Boolean {
                    return isLoading
                }

            })
        }
    }

    private fun loadFirstPage() {
        binding.apply {
            launch {
                jobsViewModel.loadFirstPage()
                    .collect {
                        when (it) {
                            is JobResource.Error -> {
                                Toast.makeText(
                                    requireContext(),
                                    "something went wrong",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            is JobResource.Loading -> {
                                progress.visibility = View.VISIBLE
                            }

                            is JobResource.Success -> {
                                progress.visibility = View.GONE
                                getTotalPages(it.jobList)
                            }

                        }
                    }
            }
        }
    }

    private fun getTotalPages(jobList: List<JobResponse>) {
        launch {
            jobsViewModel.countJobs()
                .collect {
                    when (it) {
                        is ApiStatus.Error -> {
                            Log.e(TAG, "getTotalPages: total pages failed")
                        }

                        is ApiStatus.Loading -> {
                            Log.d(TAG, "getTotalPages: total pages loading")
                        }

                        is ApiStatus.Success -> {
                            TOTAL_PAGES = ceil((it.response as Int).toDouble() / 10).toInt()
                            jobAdapter.addAll(jobList)
                            if (currentPage <= TOTAL_PAGES) {
                                jobAdapter.addLoadingFooter()
                            } else {
                                isLastPage = true
                            }
                        }
                    }
                }
        }
    }

    private fun loadNextPage() {
        launch {
            jobsViewModel.loadNextPage(currentPage)
                .collect {
                    when (it) {
                        is JobResource.Error -> {

                        }

                        is JobResource.Loading -> {

                        }

                        is JobResource.Success -> {
                            jobAdapter.removeLoadingFooter()
                            isLoading = false
                            jobAdapter.addAll(it.jobList)

                            if (currentPage != TOTAL_PAGES) {
                                jobAdapter.addLoadingFooter()
                            } else {
                                isLastPage = true
                            }
                        }
                    }
                }
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