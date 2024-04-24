package dev.goblingroup.uzworks.fragments.profile

import android.content.Context
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
import dev.goblingroup.uzworks.utils.ConstValues.ANNOUNCEMENT_ADDING
import dev.goblingroup.uzworks.utils.ConstValues.ANNOUNCEMENT_ADD_EDIT_STATUS
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.ProfileViewModel
import kotlin.math.log

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val TAG = "ProfileFragment"

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    private var userResponse: UserResponse? = null

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
                val bundle = Bundle()
                bundle.putParcelable("user_response", userResponse)
                Log.d(TAG, "onViewCreated: $userResponse")
                findNavController().navigate(
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
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: checking UzWorks")
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
        val bundle = Bundle()
        bundle.putString(ANNOUNCEMENT_ADD_EDIT_STATUS, ANNOUNCEMENT_ADDING)
        val direction = if (profileViewModel.isEmployee()) {
            R.id.action_startFragment_to_addEditWorkerFragment
        } else {
            R.id.action_startFragment_to_addEditJobFragment
        }
        findNavController().navigate(
            resId = direction,
            args = bundle
        )
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach: checking UzWorks")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: checking UzWorks")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach: checking UzWorks")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: checking UzWorks")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: checking UzWorks")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: checking UzWorks")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: checking UzWorks")
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