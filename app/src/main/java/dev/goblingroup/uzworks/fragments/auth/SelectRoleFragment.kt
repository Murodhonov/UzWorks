package dev.goblingroup.uzworks.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentSelectRoleBinding
import dev.goblingroup.uzworks.utils.UserRole
import dev.goblingroup.uzworks.vm.SharedSignUpViewModel

@AndroidEntryPoint
class SelectRoleFragment : Fragment() {

    private var _binding: FragmentSelectRoleBinding? = null
    private val binding get() = _binding!!

    private val sharedSignUpViewModel: SharedSignUpViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectRoleBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            employerBtn.setOnClickListener {
                if (sharedSignUpViewModel.isEmployee()) {
                    selectEmployer()
                    sharedSignUpViewModel.setSelectedRole(UserRole.EMPLOYER.roleName)
                }
            }

            employerTv.setOnClickListener {
                if (sharedSignUpViewModel.isEmployee()) {
                    selectEmployer()
                    sharedSignUpViewModel.setSelectedRole(UserRole.EMPLOYER.roleName)
                }
            }

            employeeBtn.setOnClickListener {
                if (sharedSignUpViewModel.isEmployer()) {
                    selectEmployee()
                    sharedSignUpViewModel.setSelectedRole(UserRole.EMPLOYEE.roleName)
                }
            }

            employeeTv.setOnClickListener {
                if (sharedSignUpViewModel.isEmployer()) {
                    selectEmployee()
                    sharedSignUpViewModel.setSelectedRole(UserRole.EMPLOYEE.roleName)
                }
            }

            signUpBtn.setOnClickListener {
                if (sharedSignUpViewModel.isRoleSelected()) {
                    sharedSignUpViewModel.selectedRole.observe(viewLifecycleOwner) {
                        val bundle = Bundle()
                        bundle.putString("user role", it)
                        findNavController().navigate(
                            resId = R.id.action_selectRoleFragment_to_signUpFragment,
                            args = bundle
                        )
                    }
                } else Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.select_role),
                    Toast.LENGTH_SHORT
                ).show()
            }

            backLoginBtn.setOnClickListener {
                backToLogin()
            }

            back.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun selectEmployer() {
        binding.apply {
            employerBtn.cardElevation = 0F
            employerBtn.strokeWidth = (2 * resources.displayMetrics.density).toInt()
            employerCircle.setBackgroundResource(R.drawable.selected_circle_background)
            employerTv.setTextColor(resources.getColor(R.color.black_blue))

            employeeBtn.cardElevation = 3 * resources.displayMetrics.density
            employeeBtn.strokeWidth = 0
            employeeCircle.setBackgroundResource(R.drawable.unselected_circle_background)
            employeeTv.setTextColor(resources.getColor(R.color.black_blue_60))
        }
    }

    private fun selectEmployee() {
        binding.apply {
            employeeBtn.cardElevation = 0F
            employeeBtn.strokeWidth = (2 * resources.displayMetrics.density).toInt()
            employeeCircle.setBackgroundResource(R.drawable.selected_circle_background)
            employeeTv.setTextColor(resources.getColor(R.color.black_blue))

            employerBtn.cardElevation = 3 * resources.displayMetrics.density
            employerBtn.strokeWidth = 0
            employerCircle.setBackgroundResource(R.drawable.unselected_circle_background)
            employerTv.setTextColor(resources.getColor(R.color.black_blue_60))
        }
    }

    override fun onResume() {
        super.onResume()
        sharedSignUpViewModel.selectedRole.observe(viewLifecycleOwner) {
            when (it) {
                UserRole.EMPLOYER.roleName -> {
                    selectEmployer()
                }

                UserRole.EMPLOYEE.roleName -> {
                    selectEmployee()
                }

                else -> {}
            }
        }
    }

    private fun backToLogin() {
        findNavController().popBackStack(R.id.loginFragment, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}