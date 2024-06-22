package dev.goblingroup.uzworks.fragments.announcement

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.adapter.MyJobsAdapter
import dev.goblingroup.uzworks.adapter.MyWorkersAdapter
import dev.goblingroup.uzworks.databinding.ConfirmDeleteBinding
import dev.goblingroup.uzworks.databinding.FragmentMyAnnouncementsBinding
import dev.goblingroup.uzworks.databinding.IconsExplanationDialogBinding
import dev.goblingroup.uzworks.databinding.LoadingDialogItemBinding
import dev.goblingroup.uzworks.databinding.MyAnnouncementBottomBinding
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

    private lateinit var loadingDialog: AlertDialog
    private lateinit var loadingDialogItemBinding: LoadingDialogItemBinding

    private lateinit var myAnnouncementBottomDialog: BottomSheetDialog
    private lateinit var myAnnouncementBottomBinding: MyAnnouncementBottomBinding
    private lateinit var confirmDeleteBottomSheet: BottomSheetDialog
    private lateinit var confirmDeleteBinding: ConfirmDeleteBinding

    private lateinit var iconsExplanationBottomDialog: BottomSheetDialog
    private lateinit var iconsExplanationDialogBinding: IconsExplanationDialogBinding

    private lateinit var myJobsAdapter: MyJobsAdapter
    private lateinit var myWorkersAdapter: MyWorkersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyAnnouncementsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        iconsExplanationBottomDialog = BottomSheetDialog(requireContext())
        iconsExplanationBottomDialog = BottomSheetDialog(requireContext())
        iconsExplanationDialogBinding = IconsExplanationDialogBinding.inflate(layoutInflater)
        iconsExplanationBottomDialog.setContentView(iconsExplanationDialogBinding.root)
        iconsExplanationBottomDialog.show()
        iconsExplanationDialogBinding.cloBtn.setOnClickListener {
            iconsExplanationBottomDialog.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            back.setOnClickListener {
                findNavController().popBackStack()
            }
            myAnnouncementsViewModel.fetchAnnouncements()

            if (myAnnouncementsViewModel.isEmployee()) {
                loadWorkers()
            } else if (myAnnouncementsViewModel.isEmployer()) {
                loadJobs()
            }

            swipeRefresh.setOnRefreshListener {
                myAnnouncementsViewModel.fetchAnnouncements()
            }
        }
    }

    private fun loadJobs() {
        lifecycleScope.launch {
            myAnnouncementsViewModel.loadJobs().observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        Toast.makeText(requireContext(), resources.getString(R.string.fetch_job_failed), Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE
                    }

                    is ApiStatus.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is ApiStatus.Success -> {
                        Log.d(TAG, "loadJobs: succeeded ${it.response}")
                        binding.progressBar.visibility = View.GONE
                        binding.swipeRefresh.isRefreshing = false
                        setJobs()
                    }
                }
            }
        }
    }

    private fun setJobs() {
        binding.apply {
            myJobsAdapter = MyJobsAdapter(
                jobList = myAnnouncementsViewModel.jobList,
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

    private fun loadWorkers() {
        lifecycleScope.launch {
            myAnnouncementsViewModel.loadWorkers().observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        Toast.makeText(
                            requireContext(),
                            resources.getString(R.string.fetch_workers_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.progressBar.visibility = View.GONE
                    }

                    is ApiStatus.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is ApiStatus.Success -> {
                        Log.d(TAG, "loadWorkers: succeeded ${it.response}")
                        binding.progressBar.visibility = View.GONE
                        binding.swipeRefresh.isRefreshing = false
                        setWorkers()
                    }
                }
            }
        }
    }

    private fun setWorkers() {
        binding.apply {
            myWorkersAdapter = MyWorkersAdapter(
                jobList = myAnnouncementsViewModel.workerList,
                resources = resources,
            ) { jobId ->
                showBottom(jobId, AnnouncementEnum.WORKER.announcementType)
            }
            myAnnouncementsRv.adapter = myWorkersAdapter
            if (myWorkersAdapter.itemCount == 0) {
                noAnnouncementsTv.visibility = View.VISIBLE
            } else {
                noAnnouncementsTv.visibility = View.GONE
            }
        }
    }

    private fun showBottom(announcementId: String, announcementType: String) {
        val bundle = Bundle()
        bundle.putString("announcement_id", announcementId)
        try {
            myAnnouncementBottomDialog.show()
        } catch (e: Exception) {
            myAnnouncementBottomDialog = BottomSheetDialog(requireContext())
            myAnnouncementBottomBinding = MyAnnouncementBottomBinding.inflate(layoutInflater)
            myAnnouncementBottomDialog.setContentView(myAnnouncementBottomBinding.root)
            myAnnouncementBottomDialog.show()
        }
        myAnnouncementBottomBinding.apply {
            seeMoreBtn.setOnClickListener {
                val direction = when (announcementType) {
                    AnnouncementEnum.JOB.announcementType -> {
                        R.id.action_myAnnouncementsFragment_to_jobDetailsFragment
                    }

                    AnnouncementEnum.WORKER.announcementType -> {
                        R.id.action_myAnnouncementsFragment_to_workerDetailsFragment
                    }

                    else -> 0
                }
                myAnnouncementBottomDialog.dismiss()
                findNavController().navigate(resId = direction, args = bundle)
            }
            editBtn.setOnClickListener {
                val direction = when (announcementType) {
                    AnnouncementEnum.JOB.announcementType -> {
                        R.id.action_myAnnouncementsFragment_to_editJobFragment
                    }

                    AnnouncementEnum.WORKER.announcementType -> {
                        R.id.action_myAnnouncementsFragment_to_editWorkerFragment
                    }

                    else -> {
                        0
                    }
                }
                myAnnouncementBottomDialog.dismiss()
                findNavController().navigate(resId = direction, args = bundle)
            }
            deleteBtn.setOnClickListener {
                myAnnouncementBottomDialog.dismiss()
                confirmDelete(announcementId, announcementType)
            }
        }
    }

    private fun confirmDelete(announcementId: String, announcementType: String) {
        try {
            confirmDeleteBottomSheet.show()
        } catch (e: Exception) {
            confirmDeleteBottomSheet = BottomSheetDialog(requireContext())
            confirmDeleteBinding = ConfirmDeleteBinding.inflate(layoutInflater)
            confirmDeleteBottomSheet.setContentView(confirmDeleteBinding.root)
            confirmDeleteBottomSheet.show()
        }
        confirmDeleteBinding.apply {
            yesBtn.setOnClickListener {
                deleteAnnouncement(announcementId, announcementType)
            }
            cancelBtn.setOnClickListener {
                confirmDeleteBottomSheet.dismiss()
                myAnnouncementBottomDialog.show()
            }
        }
    }

    private fun deleteAnnouncement(
        announcementId: String,
        announcementType: String
    ) {
        myAnnouncementsViewModel.deleteAnnouncement(announcementId)
            .observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        failed()
                    }

                    is ApiStatus.Loading -> {
                        loading()
                    }

                    is ApiStatus.Success -> {
                        success(it.response!!, announcementType)
                    }
                }
            }
    }

    private fun failed() {
        hideLoading()
    }

    private fun loading() {
        try {
            loadingDialog.show()
        } catch (e: Exception) {
            loadingDialog = AlertDialog.Builder(requireContext()).create()
            loadingDialogItemBinding = LoadingDialogItemBinding.inflate(layoutInflater)
            loadingDialog.setView(loadingDialogItemBinding.root)
            loadingDialog.setCancelable(true)
            loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            loadingDialog.show()
        }
    }

    private fun success(position: Int, announcementType: String) {
        binding.apply {
            hideLoading()
            confirmDeleteBottomSheet.dismiss()
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.announcement_deleted),
                Toast.LENGTH_SHORT
            ).show()
            when (announcementType) {
                AnnouncementEnum.JOB.announcementType -> {
                    myJobsAdapter.notifyItemRemoved(position)
                    if (myJobsAdapter.itemCount == 0) {
                        noAnnouncementsTv.visibility = View.VISIBLE
                    } else {
                        noAnnouncementsTv.visibility = View.GONE
                    }
                }

                AnnouncementEnum.WORKER.announcementType -> {
                    myWorkersAdapter.notifyItemRemoved(position)
                    if (myWorkersAdapter.itemCount == 0) {
                        noAnnouncementsTv.visibility = View.VISIBLE
                    } else {
                        noAnnouncementsTv.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun hideLoading() {
        try {
            loadingDialog.dismiss()
        } catch (e: Exception) {
            Log.e(TAG, "hideLoading: ${e.message}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}