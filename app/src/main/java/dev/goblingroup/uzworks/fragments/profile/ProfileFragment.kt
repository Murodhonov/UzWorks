package dev.goblingroup.uzworks.fragments.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentProfileBinding
import dev.goblingroup.uzworks.models.response.UserResponse
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.utils.getNavOptions
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.ProfileViewModel

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    private var userResponse: UserResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            addAnnouncementTv.text =
                if (profileViewModel.isEmployee()) resources.getString(R.string.add_worker_announcement) else resources.getString(
                    R.string.add_job_announcement
                )

            profileViewModel.userLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {

                    }

                    is ApiStatus.Loading -> {

                    }

                    is ApiStatus.Success -> {
                        userResponse = it.response
                        succeeded()
                    }
                }
            }

            settings.setOnClickListener {
                findNavController().navigate(
                    resId = R.id.settingsFragment,
                    args = null,
                    navOptions = getNavOptions()
                )
            }

            personalInfoBtn.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("user_response", userResponse)
                Log.d(TAG, "onViewCreated: $userResponse")
                findNavController().navigate(
                    resId = R.id.personalInfoFragment,
                    args = null,
                    navOptions = getNavOptions()
                )
            }

            experienceBtn.setOnClickListener {
                if (profileViewModel.isEmployee())
                    findNavController().navigate(
                        resId = R.id.experienceFragment,
                        args = null,
                        navOptions = getNavOptions()
                    )
            }

            myAnnouncementsBtn.setOnClickListener {
                findNavController().navigate(
                    resId = R.id.myAnnouncementsFragment,
                    args = null,
                    navOptions = getNavOptions()
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
        }
    }

    private fun succeeded() {
        binding.apply {
            userNameTv.text = "${userResponse?.firstName} ${userResponse?.lastName}"
            Log.d(TAG, "succeeded: $userResponse")
            when (userResponse?.gender ?: "") {
                GenderEnum.MALE.label -> avatarIv.setImageResource(R.drawable.ic_male)
                GenderEnum.FEMALE.label -> avatarIv.setImageResource(R.drawable.ic_female)
            }
        }
    }

    private fun addAnnouncement() {
        val direction = if (profileViewModel.isEmployee()) {
            R.id.addWorkerFragment
        } else {
            R.id.addJobFragment
        }
        findNavController().navigate(
            resId = direction,
            args = null,
            navOptions = getNavOptions()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}