package dev.goblingroup.uzworks.fragments.profile

import android.os.Build
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.databinding.FragmentUpdatePasswordBinding

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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            oldPasswordEt.editText?.transformationMethod =
                PasswordTransformationMethod.getInstance()
            newPasswordEt.editText?.transformationMethod =
                PasswordTransformationMethod.getInstance()
            confirmNewPasswordEt.editText?.transformationMethod =
                PasswordTransformationMethod.getInstance()

            updateBtn.setOnClickListener {
                checkPasswords()
            }

            back.setOnClickListener {
                findNavController().popBackStack()
            }

            oldPasswordEt.editText?.addTextChangedListener {
                if (oldPasswordEt.isErrorEnabled && it?.toString()
                        ?.isNotEmpty() == true
                ) {
                    oldPasswordEt.isErrorEnabled = false
                }
            }

            newPasswordEt.editText?.addTextChangedListener {
                if (newPasswordEt.isErrorEnabled && it?.toString()
                        ?.isNotEmpty() == true
                ) {
                    newPasswordEt.isErrorEnabled = false
                }
            }

            confirmNewPasswordEt.editText?.addTextChangedListener {
                if (confirmNewPasswordEt.isErrorEnabled && it?.toString()
                        ?.isNotEmpty() == true
                ) {
                    confirmNewPasswordEt.isErrorEnabled = false
                }
            }
        }
    }

    private fun checkPasswords() {
        binding.apply {
            if (oldPasswordEt.editText?.text?.toString()
                    ?.isNotEmpty() == true && newPasswordEt.editText?.text?.toString()
                    ?.isNotEmpty() == true && confirmNewPasswordEt.editText?.text?.toString()
                    ?.isNotEmpty() == true
            ) {
                if (confirmNewPasswordEt.editText?.text.toString() != newPasswordEt.editText?.text.toString()) {
                    confirmNewPasswordEt.error = "Please confirm the password"
                    confirmNewPasswordEt.isErrorEnabled = true
                } else {
                    updatePassword()
                }
            } else {
                if (oldPasswordEt.editText?.text?.toString()?.isEmpty() == true) {
                    oldPasswordEt.error = "Enter your old password"
                    oldPasswordEt.isErrorEnabled = true
                }
                if (newPasswordEt.editText?.text?.toString()?.isEmpty() == true) {
                    newPasswordEt.error = "Enter your new password"
                    newPasswordEt.isErrorEnabled = true
                }
                if (confirmNewPasswordEt.editText?.text?.toString()?.isEmpty() == true) {
                    confirmNewPasswordEt.error = "Confirm your new password"
                    confirmNewPasswordEt.isErrorEnabled = true
                }
            }
        }
    }

    private fun updatePassword() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}