package dev.goblingroup.uzworks.fragments.auth.select_role

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentSelectRoleBinding
import dev.goblingroup.uzworks.utils.ConstValues.EMPLOYEE
import dev.goblingroup.uzworks.utils.ConstValues.EMPLOYER
import dev.goblingroup.uzworks.utils.getNavOptions

class SelectRoleFragment : Fragment() {

    private var _binding: FragmentSelectRoleBinding? = null
    private val binding get() = _binding!!
    private val TAG = "SelectRoleFragment"

    private var status = EMPLOYEE

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
                selectEmployer()
            }

            employerTv.setOnClickListener {
                selectEmployer()
            }

            employeeBtn.setOnClickListener {
                selectEmployee()
            }

            employeeTv.setOnClickListener {
                selectEmployee()
            }

            signUpBtn.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("user role", status)
                findNavController().navigate(
                    resId = R.id.signUpFragment,
                    args = bundle,
                    navOptions = getNavOptions()
                )
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
            if (status != EMPLOYER) {
                employerBtn.cardElevation = 0F
                employerBtn.strokeWidth = (2 * resources.displayMetrics.density).toInt()
                employerCircle.setBackgroundResource(R.drawable.selected_circle_background)
                employerTv.setTextColor(resources.getColor(R.color.black_blue))

                employeeBtn.cardElevation = 3 * resources.displayMetrics.density
                employeeBtn.strokeWidth = 0
                employeeCircle.setBackgroundResource(R.drawable.unselected_circle_background)
                employeeTv.setTextColor(resources.getColor(R.color.black_blue_60))
            }
            status = EMPLOYER
        }
    }

    private fun selectEmployee() {
        binding.apply {
            if (status != EMPLOYEE) {
                employeeBtn.cardElevation = 0F
                employeeBtn.strokeWidth = (2 * resources.displayMetrics.density).toInt()
                employeeCircle.setBackgroundResource(R.drawable.selected_circle_background)
                employeeTv.setTextColor(resources.getColor(R.color.black_blue))

                employerBtn.cardElevation = 3 * resources.displayMetrics.density
                employerBtn.strokeWidth = 0
                employerCircle.setBackgroundResource(R.drawable.unselected_circle_background)
                employerTv.setTextColor(resources.getColor(R.color.black_blue_60))
            }
            status = EMPLOYEE
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