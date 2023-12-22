package dev.goblingroup.uzworks.fragments.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.database.AppDatabase
import dev.goblingroup.uzworks.database.entity.UserEntity
import dev.goblingroup.uzworks.databinding.FragmentProfileBinding
import dev.goblingroup.uzworks.utils.NetworkHelper
import dev.goblingroup.uzworks.service.UserRoleImpl
import dev.goblingroup.uzworks.service.UserRoleService
import dev.goblingroup.uzworks.utils.ConstValues.SUPER_ADMIN
import dev.goblingroup.uzworks.utils.aboutDialog
import dev.goblingroup.uzworks.utils.experienceDialog
import dev.goblingroup.uzworks.utils.fieldsDialog
import dev.goblingroup.uzworks.utils.getNavOptions
import dev.goblingroup.uzworks.utils.personalInfoDialog

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var appDatabase: AppDatabase
    private lateinit var networkHelper: NetworkHelper
    private lateinit var user: UserEntity
    private lateinit var userRoleService: UserRoleService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            appDatabase = AppDatabase.getInstance(requireContext())
            networkHelper = NetworkHelper(requireContext())

            settings.setOnClickListener {
                findNavController().navigate(
                    resId = R.id.settingsFragment,
                    args = null,
                    navOptions = getNavOptions()
                )
            }

            personalInfoBtn.setOnClickListener {
                personalInfoDialog(requireContext(), layoutInflater)
            }

            aboutBtn.setOnClickListener {
                aboutDialog(requireContext(), layoutInflater)
            }

            fieldsBtn.setOnClickListener {
                fieldsDialog(requireContext(), layoutInflater, resources)
            }

            experienceBtn.setOnClickListener {
                experienceDialog(requireContext(), layoutInflater)
            }

            adminPanelBtn.setOnClickListener {
                findNavController().navigate(
                    resId = R.id.adminPanelFragment,
                    args = null,
                    navOptions = getNavOptions()
                )
            }


            user = appDatabase.userDao().getUser()!!
            userRoleService = UserRoleImpl()
            val userRoleList = userRoleService.getUserRoleList(requireContext())
            if (userRoleList.contains(SUPER_ADMIN)) {
                adminPanelBtn.visibility = View.VISIBLE
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}