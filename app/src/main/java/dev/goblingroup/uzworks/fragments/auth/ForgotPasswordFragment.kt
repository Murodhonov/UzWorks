package dev.goblingroup.uzworks.fragments.auth

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.ForgotPasswordDialogBinding
import dev.goblingroup.uzworks.databinding.FragmentForgotPasswordBinding
import dev.goblingroup.uzworks.databinding.LoadingDialogItemBinding
import dev.goblingroup.uzworks.databinding.NotFoundDialogBinding
import dev.goblingroup.uzworks.models.request.ForgotPasswordRequest
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.request.ResetPasswordRequest
import dev.goblingroup.uzworks.utils.addCodeTextWatcher
import dev.goblingroup.uzworks.utils.formatPhoneNumber
import dev.goblingroup.uzworks.utils.setFocus
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.AuthApiStatus
import dev.goblingroup.uzworks.vm.ForgotPasswordViewModel
import dev.goblingroup.uzworks.vm.LoginViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private val forgotPasswordViewModel: ForgotPasswordViewModel by viewModels()

    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var loadingDialog: AlertDialog
    private lateinit var loadingDialogItemBinding: LoadingDialogItemBinding

    private lateinit var notFoundDialog: AlertDialog
    private lateinit var notFoundDialogBinding: NotFoundDialogBinding

    private lateinit var forgotPasswordDialog: AlertDialog
    private lateinit var forgotPasswordDialogBinding: ForgotPasswordDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            submitBtn.setOnClickListener {
                if (phoneNumberEt.editText?.text.toString().trim()
                        .filter { !it.isWhitespace() }.length != 13
                ) {
                    phoneNumberEt.isErrorEnabled = true
                    phoneNumberEt.error = resources.getString(R.string.phone_number_error)
                } else {
                    lifecycleScope.launch {
                        forgotPasswordViewModel.forgotPassword(
                            ForgotPasswordRequest(
                                phoneNumberEt.editText?.text.toString().trim()
                                    .filter { !it.isWhitespace() }.substring(1)
                            )
                        ).observe(viewLifecycleOwner) {
                            when (it) {
                                is ApiStatus.Error -> {
                                    notFound()
                                }

                                is ApiStatus.Loading -> {
                                    loading()
                                }

                                is ApiStatus.Success -> {
                                    verifyPhone()
                                }
                            }
                        }
                    }
                }
            }

            phoneNumberEt.editText?.addTextChangedListener(object : TextWatcher {
                private var isFormatting = false

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                }

                override fun afterTextChanged(s: Editable?) {
                    if (isFormatting) {
                        return
                    }

                    isFormatting = true
                    val newText = s.toString().filter { !it.isWhitespace() }
                    val oldText =
                        phoneNumberEt.editText?.tag.toString().filter { !it.isWhitespace() }
                    val formattedPhone =
                        s?.filter { !it.isWhitespace() }.toString()
                            .formatPhoneNumber(newText.length < oldText.length)
                    phoneNumberEt.editText?.setText(formattedPhone)
                    phoneNumberEt.editText?.setSelection(formattedPhone.length)
                    phoneNumberEt.tag = formattedPhone

                    isFormatting = false
                }
            })

            back.setOnClickListener {
                findNavController().popBackStack()
            }

            backLoginBtn.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun notFound() {
        loadingDialog.dismiss()
        try {
            notFoundDialog.show()
        } catch (e: java.lang.Exception) {
            notFoundDialog = AlertDialog.Builder(requireContext()).create()
            notFoundDialogBinding = NotFoundDialogBinding.inflate(layoutInflater)
            notFoundDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            notFoundDialog.setView(notFoundDialogBinding.root)
            notFoundDialog.show()
        }
        notFoundDialogBinding.close.setOnClickListener {
            notFoundDialog.dismiss()
        }
    }

    private fun loading() {
        try {
            loadingDialog.show()
        } catch (e: Exception) {
            loadingDialog = AlertDialog.Builder(requireContext()).create()
            loadingDialogItemBinding = LoadingDialogItemBinding.inflate(layoutInflater)
            loadingDialog.setView(loadingDialogItemBinding.root)
            loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            loadingDialog.setCancelable(false)
            loadingDialog.show()
        }
    }

    private fun verifyPhone() {
        loadingDialog.dismiss()
        try {
            forgotPasswordDialog.show()
        } catch (e: Exception) {
            forgotPasswordDialog = AlertDialog.Builder(requireContext()).create()
            forgotPasswordDialogBinding = ForgotPasswordDialogBinding.inflate(layoutInflater)
            forgotPasswordDialog.setView(forgotPasswordDialogBinding.root)
            forgotPasswordDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            forgotPasswordDialog.setCancelable(false)
            forgotPasswordDialog.show()
        }

        forgotPasswordDialogBinding.apply {
            code1.setFocus(requireActivity())
            code1.addCodeTextWatcher(requireActivity(), null, code2)
            code2.addCodeTextWatcher(requireActivity(), code1, code3)
            code3.addCodeTextWatcher(requireActivity(), code2, code4)
            code4.addCodeTextWatcher(requireActivity(), code3, passwordEt.editText)

            cancelBtn.setOnClickListener {
                val inputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    forgotPasswordDialog.window?.decorView?.windowToken,
                    0
                )
                forgotPasswordDialog.dismiss()
            }

            verifySetPasswordBtn.setOnClickListener {
                if (forgotPasswordViewModel.isFormValid(
                        requireContext(),
                        resources,
                        code1,
                        code2,
                        code3,
                        code4,
                        passwordEt,
                        confirmPasswordEt
                    )
                ) {
                    lifecycleScope.launch {
                        forgotPasswordViewModel.resetPassword(
                            ResetPasswordRequest(
                                code = "${code1.text}${code2.text}${code3.text}${code4.text}",
                                newPassword = passwordEt.editText?.text.toString(),
                                phoneNumber = binding.phoneNumberEt.editText?.text.toString().trim()
                                    .filter { !it.isWhitespace() }.substring(1)
                            )
                        ).observe(viewLifecycleOwner) {
                            when (it) {
                                is ApiStatus.Error -> {
                                    Toast.makeText(requireContext(), "failed", Toast.LENGTH_SHORT)
                                        .show()
                                }

                                is ApiStatus.Loading -> {
                                    loading()
                                }

                                is ApiStatus.Success -> {
                                    passwordReset(passwordEt.editText?.text.toString())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun passwordReset(newPassword: String) {
        loginViewModel.login(
            LoginRequest(
                password = newPassword,
                phoneNumber = binding.phoneNumberEt.editText?.text.toString().trim()
                    .filter { !it.isWhitespace() }.substring(1)
            )
        ).observe(viewLifecycleOwner) {
            when (it) {
                is AuthApiStatus.Error -> {
                    Toast.makeText(requireContext(), "failed to log you in", Toast.LENGTH_SHORT)
                        .show()
                }

                is AuthApiStatus.Loading -> {

                }

                is AuthApiStatus.Success -> {
                    forgotPasswordDialog.dismiss()
                    loadingDialog.dismiss()
                    findNavController().navigate(
                        resId = R.id.action_forgotPasswordFragment_to_startFragment,
                        args = null
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}