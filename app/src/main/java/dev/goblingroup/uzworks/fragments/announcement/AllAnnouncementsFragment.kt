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
import dev.goblingroup.uzworks.adapter.AllAnnouncementsAdapter
import dev.goblingroup.uzworks.databinding.FragmentAllAnnouncementsBinding
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

    private lateinit var allAnnouncementsAdapter: AllAnnouncementsAdapter

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
            loadCategories()
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
                            progress.visibility = View.GONE
                            noAnnouncementsTv.visibility = View.VISIBLE
                        }

                        is ApiStatus.Loading -> {
                            Log.d(TAG, "loadCategories: loading")
                            progress.visibility = View.VISIBLE
                            noAnnouncementsTv.visibility = View.GONE
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
                            noAnnouncementsTv.visibility = View.VISIBLE
                            progress.visibility = View.GONE
                        }
                        is ApiStatus.Loading -> {
                            Log.d(TAG, "loadAddresses: loading")
                            progress.visibility = View.VISIBLE
                            noAnnouncementsTv.visibility = View.GONE
                        }
                        is ApiStatus.Success -> {
                            Log.d(TAG, "loadAddresses: succeeded <${it.response?.size}>")
                            loadAnnouncements()
                        }
                    }
                }
            }
        }
    }

    private fun loadAnnouncements() {
        lifecycleScope.launch {
            announcementViewModel.announcementLiveData.observe(viewLifecycleOwner) {
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
                        success()
                    }
                }
            }
        }
    }

    private fun success() {
        if (_binding != null) {
            binding.apply {
                progress.visibility = View.GONE
                allAnnouncementsAdapter = AllAnnouncementsAdapter(
                    announcementViewModel,
                    jobCategoryViewModel,
                    addressViewModel,
                    resources,
                    { announcementId, announcementType ->
                        Log.d(TAG, "success: checking announcement details $announcementId $announcementType")
                        announcementClickListener?.onAllAnnouncementClick(announcementId, announcementType)
                    }, { state, announcementId ->
                        notifySaveUnSave(state, announcementId)
                    }
                )
                Log.d(
                    TAG,
                    "success: called in ${this@AllAnnouncementsFragment::class.java.simpleName}"
                )
                recommendedWorkAnnouncementsRv.adapter = allAnnouncementsAdapter
                if (allAnnouncementsAdapter.itemCount == 0) {
                    noAnnouncementsTv.visibility = View.VISIBLE
                } else {
                    noAnnouncementsTv.visibility = View.GONE
                }
            }
        }
    }

    private fun notifySaveUnSave(state: Boolean, announcementId: String) {

    }

    interface AllAnnouncementClickListener {
        fun onAllAnnouncementClick(announcementId: String, announcementType: String)
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