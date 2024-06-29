package com.goblindevs.uzworks.fragments.announcement

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import com.goblindevs.uzworks.R
import com.goblindevs.uzworks.adapter.SavedAnnouncementsAdapter
import com.goblindevs.uzworks.databinding.FragmentSavedAnnouncementsBinding
import com.goblindevs.uzworks.databinding.SavedAnnouncementBottomBinding
import com.goblindevs.uzworks.utils.AnnouncementEnum
import com.goblindevs.uzworks.vm.SavedAnnouncementsViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SavedAnnouncementsFragment : Fragment() {

    private var _binding: FragmentSavedAnnouncementsBinding? = null
    private val binding get() = _binding!!

    private var findAnnouncementClickListener: FindAnnouncementClickListener? = null

    private lateinit var savedAnnouncementsAdapter: SavedAnnouncementsAdapter

    private val savedAnnouncementsViewModel: SavedAnnouncementsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedAnnouncementsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            findAnnouncementBtn.setOnClickListener {
                findAnnouncementClickListener?.onFindAnnouncementClick()
            }
        }
    }

    private fun loadSavedAnnouncements() {
        lifecycleScope.launch {
            binding.apply {
                savedAnnouncementsViewModel.getSavedAnnouncements()
                savedAnnouncementsAdapter = SavedAnnouncementsAdapter(
                    savedAnnouncementsViewModel.savedAnnouncements,
                    resources,
                    { announcementId, announcementType, positioon ->
                        showBottom(announcementId, announcementType, positioon)
                    }
                ) { announcementId, position ->
                    unSave(announcementId, position)
                }
                recommendedWorkAnnouncementsRv.adapter = savedAnnouncementsAdapter
                if (savedAnnouncementsViewModel.countAnnouncements() == 0) {
                    emptyLayout.visibility = View.VISIBLE
                } else {
                    emptyLayout.visibility = View.GONE
                }
            }
        }
    }

    private fun unSave(announcementId: String, position: Int) {
        binding.apply {
            savedAnnouncementsViewModel.unSave(announcementId)
            savedAnnouncementsViewModel.savedAnnouncements.removeAt(position)
            savedAnnouncementsAdapter.notifyItemRemoved(position)
            if (savedAnnouncementsAdapter.itemCount == 0) {
                emptyLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun showBottom(announcementId: String, announcementType: String, position: Int) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val savedAnnouncementBottomBinding = SavedAnnouncementBottomBinding.inflate(layoutInflater)
        savedAnnouncementBottomBinding.apply {
            bottomSheetDialog.setContentView(root)
            shareBtn.setOnClickListener {
                bottomSheetDialog.dismiss()
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, "public link is in progress")
                startActivity(Intent.createChooser(shareIntent, "Share via"))
            }
            seeMoreBtn.setOnClickListener {
                bottomSheetDialog.dismiss()
                val direction =
                    if (announcementType == AnnouncementEnum.JOB.announcementType)
                        R.id.action_startFragment_to_jobDetailsFragment
                    else
                        R.id.action_startFragment_to_workerDetailsFragment
                val bundle = Bundle()
                bundle.putString("announcement_id", announcementId)
                findNavController().navigate(
                    resId = direction,
                    args = bundle
                )
            }
            deleteBtn.setOnClickListener {
                bottomSheetDialog.dismiss()
                unSave(announcementId, position)
            }
        }
        bottomSheetDialog.show()
    }

    interface FindAnnouncementClickListener {
        fun onFindAnnouncementClick()
    }

    fun setOnFindAnnouncementClickListener(listener: FindAnnouncementClickListener) {
        findAnnouncementClickListener = listener
    }

    override fun onResume() {
        super.onResume()
        loadSavedAnnouncements()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        fun newInstance() =
            SavedAnnouncementsFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}