package com.goblindevs.uzworks.fragments.announcement

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import com.goblindevs.uzworks.R
import com.goblindevs.uzworks.adapter.AllAnnouncementsAdapter
import com.goblindevs.uzworks.databinding.FragmentAllAnnouncementsBinding
import com.goblindevs.uzworks.utils.AnnouncementEnum
import com.goblindevs.uzworks.utils.ConstValues.TAG
import com.goblindevs.uzworks.vm.AllAnnouncementsViewModel
import com.goblindevs.uzworks.vm.ApiStatus
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllAnnouncementsFragment : Fragment() {

    private var _binding: FragmentAllAnnouncementsBinding? = null
    private val binding get() = _binding!!

    private lateinit var allAnnouncementsAdapter: AllAnnouncementsAdapter

    private val allAnnouncementsViewModel: AllAnnouncementsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllAnnouncementsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            loadAnnouncements()

            root.setOnRefreshListener {
                allAnnouncementsViewModel.fetchData()
            }
        }
    }

    private fun loadAnnouncements() {
        lifecycleScope.launch {
            allAnnouncementsViewModel.announcementLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        Log.e(TAG, "onViewCreated: ${it.error}")
                        Log.e(TAG, "onViewCreated: ${it.error.printStackTrace()}")
                        Log.e(TAG, "onViewCreated: ${it.error.stackTrace}")
                        Log.e(TAG, "onViewCreated: ${it.error.message}")
                    }

                    is ApiStatus.Loading -> {
                        binding.progress.visibility = View.VISIBLE
                    }

                    is ApiStatus.Success -> {
                        binding.root.isRefreshing = false
                        Log.d(TAG, "onViewCreated: succeeded ${it.response}")
                        success(it.response!!)
                    }
                }
            }
        }
    }

    private fun success(response: List<Any>) {
        if (_binding != null) {
            binding.apply {
                progress.visibility = View.GONE
                allAnnouncementsAdapter = AllAnnouncementsAdapter(
                    allAnnouncementsViewModel,
                    response,
                    resources,
                    { announcementId, announcementType ->
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
                ) { state, announcementId ->
                    notifySaveUnSave(state, announcementId)
                }
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