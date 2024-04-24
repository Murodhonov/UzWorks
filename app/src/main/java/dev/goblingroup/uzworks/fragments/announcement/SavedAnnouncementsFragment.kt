package dev.goblingroup.uzworks.fragments.announcement

import android.content.Intent
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.adapter.SavedAnnouncementsAdapter
import dev.goblingroup.uzworks.databinding.FragmentSavedAnnouncementsBinding
import dev.goblingroup.uzworks.databinding.SavedAnnouncementBottomBinding
import dev.goblingroup.uzworks.utils.AnnouncementEnum
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.vm.AddressViewModel
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.JobCategoryViewModel
import dev.goblingroup.uzworks.vm.SavedAnnouncementsViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SavedAnnouncementsFragment : Fragment() {

    private var _binding: FragmentSavedAnnouncementsBinding? = null
    private val binding get() = _binding!!

    private var findAnnouncementClickListener: FindAnnouncementClickListener? = null

    private lateinit var savedAnnouncementsAdapter: SavedAnnouncementsAdapter

    private val savedAnnouncementsViewModel: SavedAnnouncementsViewModel by viewModels()
    private val jobCategoryViewModel: JobCategoryViewModel by viewModels()
    private val addressViewModel: AddressViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedAnnouncementsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            loadCategories()
            findAnnouncementBtn.setOnClickListener {
                findAnnouncementClickListener?.onFindAnnouncementClick()
            }
        }
    }

    private fun loadCategories() {
        binding.apply {
            lifecycleScope.launch {
                Log.d(TAG, "loadCategories: started")
                jobCategoryViewModel.jobCategoriesLiveData.observe(viewLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {
                            Toast.makeText(requireContext(), "some error", Toast.LENGTH_SHORT)
                                .show()
                            Log.d(TAG, "loadCategories: failed")
                            Log.e(TAG, "loadCategories: ${it.error}")
                            Log.e(TAG, "loadCategories: ${it.error.printStackTrace()}")
                            Log.e(TAG, "loadCategories: ${it.error.stackTrace}")
                            Log.e(TAG, "loadCategories: ${it.error.message}")
                            emptyLayout.visibility = View.VISIBLE
                        }

                        is ApiStatus.Loading -> {
                            Log.d(TAG, "loadCategories: loading")
                            emptyLayout.visibility = View.GONE
                        }

                        is ApiStatus.Success -> {
                            Log.d(TAG, "loadCategories: succeeded <${it.response?.size}>")
                            loadAddresses()
                        }

                        else -> {

                        }
                    }
                }
            }
        }
    }

    private fun loadAddresses() {
        binding.apply {
            lifecycleScope.launch {
                Log.d(TAG, "loadAddresses: started")
                addressViewModel.districtLiveData.observe(viewLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {
                            Toast.makeText(
                                requireContext(),
                                "some error while loading regions or districts",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e(TAG, "loadAddresses: failed")
                            emptyLayout.visibility = View.VISIBLE
                        }

                        is ApiStatus.Loading -> {
                            Log.d(TAG, "loadAddresses: loading")
                            emptyLayout.visibility = View.GONE
                        }

                        is ApiStatus.Success -> {
                            Log.d(TAG, "loadAddresses: succeeded <${it.response?.size}>")
                            loadSavedAnnouncements()
                        }
                    }
                }
            }
        }
    }

    private fun loadSavedAnnouncements() {
        lifecycleScope.launch {
            binding.apply {
                savedAnnouncementsAdapter = SavedAnnouncementsAdapter(
                    savedAnnouncementsViewModel,
                    savedAnnouncementsViewModel.getSavedAnnouncements(),
                    resources,
                    { announcementId, announcementType ->
                        showBottom(announcementId, announcementType)
                    }
                ) { isEmpty, announcementId ->
                    notifyUnSave(isEmpty, announcementId)
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

    private fun showBottom(announcementId: String, announcementType: String) {
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
                Toast.makeText(
                    requireContext(),
                    "delete job from saved is in progress",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        bottomSheetDialog.show()
    }

    private fun notifyUnSave(isEmpty: Boolean, announcementId: String) {
        binding.apply {
            if (isEmpty) emptyLayout.visibility = View.VISIBLE
        }
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