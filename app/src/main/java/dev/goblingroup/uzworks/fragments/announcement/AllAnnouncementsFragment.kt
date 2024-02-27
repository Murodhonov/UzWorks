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
import dev.goblingroup.uzworks.adapter.rv_adapters.AnnouncementsAdapter
import dev.goblingroup.uzworks.databinding.FragmentAllAnnouncementsBinding
import dev.goblingroup.uzworks.models.CombinedData
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.vm.AddressViewModel
import dev.goblingroup.uzworks.vm.AnnouncementViewModel
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.JobCategoryViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllAnnouncementsFragment : Fragment() {

    private var _binding: FragmentAllAnnouncementsBinding? = null
    private val binding get() = _binding!!

    private var announcementClickListener: AllAnnouncementClickListener? = null

    private lateinit var announcementsAdapter: AnnouncementsAdapter

    private val announcementViewModel: AnnouncementViewModel by viewModels()
    private val jobCategoryViewModel: JobCategoryViewModel by viewModels()
    private val addressViewModel: AddressViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllAnnouncementsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            loadAnnouncements()
        }
    }

    private fun loadAnnouncements() {
        lifecycleScope.launch {
            announcementViewModel.combinedLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "failed to load announcements",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(TAG, "onViewCreated: ${it.error}")
                        Log.e(TAG, "onViewCreated: ${it.error.printStackTrace()}")
                        Log.e(TAG, "onViewCreated: ${it.error.stackTrace}")
                        Log.e(TAG, "onViewCreated: ${it.error.message}")
                    }

                    is ApiStatus.Loading -> {
                        binding.progress.visibility = View.VISIBLE
                    }

                    is ApiStatus.Success -> {
                        Log.d(TAG, "onViewCreated: succeeded ${it.response}")
                        success(it.response!!)
                    }
                }
            }
        }
    }

    private fun success(combinedData: CombinedData) {
        if (_binding != null) {
            binding.apply {
                progress.visibility = View.GONE
                announcementsAdapter = AnnouncementsAdapter(
                    combinedData,
                    jobCategoryViewModel.listJobCategories(),
                    addressViewModel = addressViewModel,
                    { announcementId ->
                        announcementClickListener?.onAllAnnouncementClick(announcementId)
                    }, { state, announcementId, _ ->
                        saveUnSave(state, announcementId)
                    }
                )
                recommendedWorkAnnouncementsRv.adapter = announcementsAdapter
            }
        }
    }

    private fun saveUnSave(state: Boolean, announcementId: String) {
        if (state) {
            announcementViewModel.saveAnnouncement(announcementId)
        } else {
            announcementViewModel.unSaveAnnouncement(announcementId)
        }
    }

    interface AllAnnouncementClickListener {
        fun onAllAnnouncementClick(announcementId: String)
    }

    fun setOnAnnouncementClickListener(listener: AllAnnouncementClickListener) {
        announcementClickListener = listener
    }

    override fun onResume() {
        super.onResume()
        loadAnnouncements()
    }

    override fun onPause() {
        super.onPause()
        loadAnnouncements()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            AllAnnouncementsFragment().apply {

            }
    }
}