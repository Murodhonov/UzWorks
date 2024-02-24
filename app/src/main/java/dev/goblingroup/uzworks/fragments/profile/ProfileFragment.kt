package dev.goblingroup.uzworks.fragments.profile

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import dev.goblingroup.uzworks.adapter.rv_adapters.ExperienceAdapter
import dev.goblingroup.uzworks.databinding.AboutDialogItemBinding
import dev.goblingroup.uzworks.databinding.ExperienceDialogItemBinding
import dev.goblingroup.uzworks.databinding.FragmentProfileBinding
import dev.goblingroup.uzworks.models.response.UserResponse
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.utils.UserRole
import dev.goblingroup.uzworks.utils.getNavOptions
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.ProfileViewModel
import dev.goblingroup.uzworks.vm.SecurityViewModel

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val securityViewModel: SecurityViewModel by viewModels()
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

            aboutBtn.setOnClickListener {
                if (userResponse != null) {
                    aboutDialog(requireContext(), layoutInflater)
                }
            }

            experienceBtn.setOnClickListener {
                if (userResponse != null) {
                    experienceDialog(requireContext(), layoutInflater)
                }
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

    private fun aboutDialog(
        context: Context,
        layoutInflater: LayoutInflater
    ) {
        val alertDialog = AlertDialog.Builder(context).create()
        alertDialog.show()
        val binding = AboutDialogItemBinding.inflate(layoutInflater)
        binding.apply {
            alertDialog.setView(root)
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    private fun experienceDialog(
        context: Context,
        layoutInflater: LayoutInflater
    ) {
        val alertDialog = AlertDialog.Builder(context).create()
        alertDialog.show()
        val binding = ExperienceDialogItemBinding.inflate(layoutInflater)
        binding.apply {
            alertDialog.setView(root)
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            experienceRv.adapter = ExperienceAdapter()
        }
    }

    private fun addAnnouncement() {
        val direction = if (securityViewModel.getUserRoles()
                .contains(UserRole.EMPLOYEE.roleName)
        ) {
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