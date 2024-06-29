package com.goblindevs.uzworks.fragments.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import com.goblindevs.uzworks.R
import com.goblindevs.uzworks.databinding.FragmentProfileBinding
import com.goblindevs.uzworks.models.response.UserResponse
import com.goblindevs.uzworks.utils.GenderEnum
import com.goblindevs.uzworks.vm.ApiStatus
import com.goblindevs.uzworks.vm.ProfileViewModel

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val TAG = "ProfileFragment"

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: checking UzWorks")
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: checking UzWorks")
        binding.apply {
            addAnnouncementTv.text =
                if (profileViewModel.isEmployee()) resources.getString(R.string.add_worker_announcement) else resources.getString(
                    R.string.add_job_announcement
                )

            settings.setOnClickListener {
                findNavController().navigate(
                    resId = R.id.action_startFragment_to_settingsFragment,
                    args = null
                )
            }

            personalInfoBtn.setOnClickListener {
                findNavController().navigate(
                    resId = R.id.action_startFragment_to_personalInfoFragment,
                    args = null
                )
            }

            addressTv.setOnClickListener {
                if (addressTv.text == resources.getString(R.string.address_unknown)) findNavController().navigate(
                    resId = R.id.action_startFragment_to_personalInfoFragment,
                    args = null
                )
            }

            experienceBtn.setOnClickListener {
                if (profileViewModel.isEmployee())
                    findNavController().navigate(
                        resId = R.id.action_startFragment_to_experienceFragment,
                        args = null
                    )
            }

            myAnnouncementsBtn.setOnClickListener {
                findNavController().navigate(
                    resId = R.id.action_startFragment_to_myAnnouncementsFragment,
                    args = null
                )
            }

            addAnnouncementBtn.setOnClickListener {
                addAnnouncement()
            }

            if (profileViewModel.isEmployer()) {
                experienceLayout.visibility = View.GONE
            } else if (profileViewModel.isEmployee()) {
                experienceLayout.visibility = View.VISIBLE
            }

            swipeRefresh.setOnRefreshListener {
                profileViewModel.fetchUserData().observe(viewLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {

                        }

                        is ApiStatus.Loading -> {

                        }

                        is ApiStatus.Success -> {
                            succeeded(it.response!!)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        profileViewModel.fetchUserData().observe(viewLifecycleOwner) {
            when (it) {
                is ApiStatus.Error -> {

                }

                is ApiStatus.Loading -> {

                }

                is ApiStatus.Success -> {
                    succeeded(it.response!!)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun succeeded(response: UserResponse) {
        binding.apply {
            swipeRefresh.isRefreshing = false
            Log.d(TAG, "succeeded: $response")
            userNameTv.text = "${response.firstName} ${response.lastName}"
            when {
                response.regionName == null && response.districtName == null -> {
                    addressTv.text = resources.getString(R.string.address_unknown)
                    addressTv.setTextColor(resources.getColor(R.color.yellow))
                }
                response.regionName != null && response.districtName == null -> {
                    addressTv.text = "${response.regionName}"
                    addressTv.setTextColor(resources.getColor(R.color.white))
                }
                response.regionName != null && response.districtName != null -> {
                    addressTv.text = "${response.regionName}, ${response.districtName}"
                    addressTv.setTextColor(resources.getColor(R.color.white))
                }
            }
            when (response.gender) {
                GenderEnum.MALE.code -> avatarIv.setImageResource(R.drawable.ic_male)
                GenderEnum.FEMALE.code -> avatarIv.setImageResource(R.drawable.ic_female)
            }
        }
    }

    private fun addAnnouncement() {
        val direction = if (profileViewModel.isEmployee()) {
            R.id.action_startFragment_to_addWorkerFragment
        } else {
            R.id.action_startFragment_to_addJobFragment
        }
        findNavController().navigate(
            resId = direction,
            args = null
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: checking UzWorks")
        _binding = null
    }

    companion object {

        fun newInstance() =
            ProfileFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}