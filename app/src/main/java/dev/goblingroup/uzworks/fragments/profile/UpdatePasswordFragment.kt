package dev.goblingroup.uzworks.fragments.profile

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.databinding.FragmentUpdatePasswordBinding
import dev.goblingroup.uzworks.utils.showHidePassword

@AndroidEntryPoint
class UpdatePasswordFragment : Fragment() {

    private var _binding: FragmentUpdatePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdatePasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            oldPasswordEt.transformationMethod = PasswordTransformationMethod.getInstance()
            newPasswordEt.transformationMethod = PasswordTransformationMethod.getInstance()
            confirmNewPasswordEt.transformationMethod = PasswordTransformationMethod.getInstance()

            oldPasswordEyeIv.setOnClickListener {
                oldPasswordEt.showHidePassword(requireContext(), oldPasswordEyeIv)
            }

            newPasswordEyeIv.setOnClickListener {
                newPasswordEt.showHidePassword(requireContext(), newPasswordEyeIv)
            }

            confirmNewPasswordEyeIv.setOnClickListener {
                confirmNewPasswordEt.showHidePassword(requireContext(), confirmNewPasswordEyeIv)
            }

            updateBtn.setOnClickListener {
                updatePassword()
            }

            back.setOnClickListener {
                findNavController().popBackStack()
            }

            oldPasswordEt.addTextChangedListener {
                if (oldPasswordErrorLayout.visibility == View.VISIBLE && it?.toString()
                        ?.isNotEmpty() == true
                ) {
                    oldPasswordErrorLayout.visibility = View.GONE
                }
            }

            newPasswordEt.addTextChangedListener {
                if (newPasswordErrorLayout.visibility == View.VISIBLE && it?.toString()
                        ?.isNotEmpty() == true
                ) {
                    newPasswordErrorLayout.visibility = View.GONE
                }
            }

            confirmNewPasswordEt.addTextChangedListener {
                if (confirmNewPasswordErrorLayout.visibility == View.VISIBLE && it?.toString()
                        ?.isNotEmpty() == true
                ) {
                    confirmNewPasswordErrorLayout.visibility = View.GONE
                }
            }
        }
    }

    private fun updatePassword() {
        binding.apply {
            if (oldPasswordEt.text?.toString()
                    ?.isNotEmpty() == true && newPasswordEt.text?.toString()
                    ?.isNotEmpty() == true && confirmNewPasswordEt.text?.toString()
                    ?.isNotEmpty() == true
            ) {
                /**
                 * check new password confirmation
                 */
                if (confirmNewPasswordEt.text.toString() != newPasswordEt.text.toString()) {
                    confirmPasswordErrorTv.text = "Please confirm the password"
                    confirmNewPasswordErrorLayout.visibility = View.VISIBLE
                } else {
                    /**
                     * new password confirmed
                     */
                    Toast.makeText(requireContext(), "password changed", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            } else {
                if (oldPasswordEt.text?.toString()?.isEmpty() == true) {
                    oldPasswordErrorLayout.visibility = View.VISIBLE
                }
                if (newPasswordEt.text?.toString()?.isEmpty() == true) {
                    newPasswordErrorLayout.visibility = View.VISIBLE
                }
                if (confirmNewPasswordEt.text?.toString()?.isEmpty() == true) {
                    confirmNewPasswordErrorLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}