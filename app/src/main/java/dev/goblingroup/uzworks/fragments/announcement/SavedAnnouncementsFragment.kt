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
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.adapter.SavedAnnouncementsAdapter
import dev.goblingroup.uzworks.databinding.FragmentSavedAnnouncementsBinding
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.vm.AddressViewModel
import dev.goblingroup.uzworks.vm.AnnouncementViewModel
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.JobCategoryViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SavedAnnouncementsFragment : Fragment() {

    private var _binding: FragmentSavedAnnouncementsBinding? = null
    private val binding get() = _binding!!

    private var savedAnnouncementClickListener: SavedAnnouncementClickListener? = null
    private var findAnnouncementClickListener: FindAnnouncementClickListener? = null

    private lateinit var savedAnnouncementsAdapter: SavedAnnouncementsAdapter

    private val announcementViewModel: AnnouncementViewModel by viewModels()
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
                    announcementViewModel,
                    jobCategoryViewModel,
                    addressViewModel,
                    resources,
                    { announcementId, announcementType ->
                        savedAnnouncementClickListener?.onSavedAnnouncementClick(announcementId, announcementType)
                    }, { isEmpty, announcementId ->
                        notifyUnSave(isEmpty, announcementId)
                    }
                )
                recommendedWorkAnnouncementsRv.adapter = savedAnnouncementsAdapter
                if (announcementViewModel.countSavedAnnouncements() == 0) {
                    emptyLayout.visibility = View.VISIBLE
                } else {
                    emptyLayout.visibility = View.GONE
                }
            }
        }
    }

    private fun notifyUnSave(isEmpty: Boolean, announcementId: String) {
        binding.apply {
            if (isEmpty) emptyLayout.visibility = View.VISIBLE
        }
    }

    interface SavedAnnouncementClickListener {
        fun onSavedAnnouncementClick(announcementId: String, announcementType: String)
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

    override fun onResume() {
        super.onResume()
        loadSavedAnnouncements()
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