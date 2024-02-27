package dev.goblingroup.uzworks.fragments.announcement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.adapter.rv_adapters.AnnouncementsAdapter
import dev.goblingroup.uzworks.databinding.FragmentSavedAnnouncementsBinding
import dev.goblingroup.uzworks.models.CombinedData
import dev.goblingroup.uzworks.vm.AddressViewModel
import dev.goblingroup.uzworks.vm.AnnouncementViewModel
import dev.goblingroup.uzworks.vm.JobCategoryViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SavedAnnouncementsFragment : Fragment() {

    private var _binding: FragmentSavedAnnouncementsBinding? = null
    private val binding get() = _binding!!

    private var savedAnnouncementClickListener: SavedAnnouncementClickListener? = null
    private var findAnnouncementClickListener: FindAnnouncementClickListener? = null

    private lateinit var announcementsAdapter: AnnouncementsAdapter

    private val announcementViewModel: AnnouncementViewModel by viewModels()
    private val jobCategoryViewModel: JobCategoryViewModel by viewModels()
    private val addressViewModel: AddressViewModel by viewModels()

    private lateinit var savedAnnouncements: CombinedData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedAnnouncementsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            loadSavedAnnouncements()
            findAnnouncementBtn.setOnClickListener {
                findAnnouncementClickListener?.onFindAnnouncementClick()
            }
        }
    }

    private fun loadSavedAnnouncements() {
        lifecycleScope.launch {
            binding.apply {
                savedAnnouncements = announcementViewModel.listSavedAnnouncements()!!
                if (savedAnnouncements.workers.isNullOrEmpty() && savedAnnouncements.jobs.isNullOrEmpty()) {
                    emptyLayout.visibility = View.VISIBLE
                } else {
                    emptyLayout.visibility = View.GONE
                    announcementsAdapter = AnnouncementsAdapter(
                        savedAnnouncements,
                        jobCategoryViewModel.listJobCategories(),
                        addressViewModel = addressViewModel,
                        { announcementId ->
                            savedAnnouncementClickListener?.onSavedAnnouncementClick(announcementId)
                        }, { _, announcementId, position ->
                            unSave(announcementId, position)
                        }
                    )
                    recommendedWorkAnnouncementsRv.adapter = announcementsAdapter
                }
            }
        }
    }

    private fun unSave(announcementId: String, position: Int) {
        binding.apply {
            if (!savedAnnouncements.workers.isNullOrEmpty()) {
                savedAnnouncements.workers?.removeAt(position)
                announcementsAdapter.notifyItemRemoved(position)
                announcementsAdapter.notifyItemRangeChanged(
                    position,
                    announcementViewModel.countSavedAnnouncements() - position
                )
                if (!announcementViewModel.unSaveAnnouncement(announcementId)!!) {
                    emptyLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    interface SavedAnnouncementClickListener {
        fun onSavedAnnouncementClick(announcementId: String)
    }

    interface FindAnnouncementClickListener {
        fun onFindAnnouncementClick()
    }

    fun setOnFindAnnouncementClickListener(listener: FindAnnouncementClickListener) {
        findAnnouncementClickListener = listener
    }

    fun setOnAnnouncementClickListener(listener: SavedAnnouncementClickListener) {
        savedAnnouncementClickListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            SavedAnnouncementsFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}