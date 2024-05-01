package dev.goblingroup.uzworks.fragments.auth

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentLoginBinding
import dev.goblingroup.uzworks.databinding.LoadingDialogItemBinding
import dev.goblingroup.uzworks.databinding.NotFoundDialogBinding
import dev.goblingroup.uzworks.databinding.PhoneVerificationDialogBinding
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.request.VerifyPhoneRequest
import dev.goblingroup.uzworks.models.response.ErrorResponse
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.LanguageSelectionListener
import dev.goblingroup.uzworks.utils.LoginError
import dev.goblingroup.uzworks.utils.addCodeTextWatcher
import dev.goblingroup.uzworks.utils.setFocus
import dev.goblingroup.uzworks.vm.AuthApiStatus
import dev.goblingroup.uzworks.vm.LoginViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by activityViewModels()

    private lateinit var loginLoading: AlertDialog
    private lateinit var loginLoadingDialogBinding: LoadingDialogItemBinding

    private lateinit var notFoundDialog: AlertDialog
    private lateinit var notFoundDialogBinding: NotFoundDialogBinding

    private lateinit var phoneVerificationDialog: BottomSheetDialog
    private lateinit var phoneVerificationDialogBinding: PhoneVerificationDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            signUpTv.setOnClickListener {
                findNavController().navigate(
                    resId = R.id.action_loginFragment_to_selectRoleFragment,
                    args = null
                )
            }

            loginBtn.setOnClickListener {
                if (loginViewModel.isFormValid(phoneNumberEt, passwordEt, resources)) {
                    login()
                }
            }

            languageBtn.setOnClickListener {
                chooseLanguage()
            }

            languageTv.setOnClickListener {
                chooseLanguage()
            }

            loginViewModel.controlInput(phoneNumberEt, passwordEt)

            languageTv.text = loginViewModel.getLanguageName()

            forgotPasswordTv.setOnClickListener {
                findNavController().navigate(
                    resId = R.id.action_loginFragment_to_forgotPasswordFragment,
                    args = null
                )
            }
        }
    }

    private fun login() {
        binding.apply {
            lifecycleScope.launch {
                loginViewModel.login(
                    loginRequest = LoginRequest(
                        password = passwordEt.editText?.text.toString(),
                        phoneNumber = phoneNumberEt.editText?.text.toString().trim()
                            .filter { !it.isWhitespace() }.substring(1)
                    )
                )
                    .observe(viewLifecycleOwner
                    ) {
                        when (it) {
                            is AuthApiStatus.Error -> {
                                loginError(it.errorResponse)
                            }

                            is AuthApiStatus.Loading -> {
                                loginLoading()
                            }

                            is AuthApiStatus.Success -> {
                                loginSuccess()
                            }
                        }
                    }
            }
        }
    }

    private fun loginError(errorResponse: ErrorResponse) {
        binding.apply {
            loginLoading.dismiss()
            Log.e(
                TAG,
                "login: in ${this@LoginFragment::class.java.simpleName} ${errorResponse.message}"
            )

            when {
                errorResponse.message == LoginError.NOT_FOUND.errorMessage -> {
                    notFound()
                }

                errorResponse.message.startsWith(LoginError.VERIFY_PHONE_NUMBER.errorMessage) -> {
                    verifyPhone()
                }

                errorResponse.message == LoginError.WRONG_PASSWORD.errorMessage -> {
                    passwordEt.isErrorEnabled = true
                    passwordEt.error = resources.getString(R.string.password_incorrect)
                }
            }
        }
    }

    private fun notFound() {
        try {
            notFoundDialog.show()
        } catch (e: Exception) {
            notFoundDialog = AlertDialog.Builder(requireContext()).create()
            notFoundDialogBinding = NotFoundDialogBinding.inflate(layoutInflater)
            notFoundDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            notFoundDialog.setView(notFoundDialogBinding.root)
            notFoundDialog.setCancelable(false)
            notFoundDialog.show()
        }
        notFoundDialogBinding.close.setOnClickListener {
            notFoundDialog.dismiss()
        }
    }

    private fun verifyPhone() {
        try {
            phoneVerificationDialog.show()
        } catch (e: Exception) {
            phoneVerificationDialog = BottomSheetDialog(requireContext())
            phoneVerificationDialogBinding = PhoneVerificationDialogBinding.inflate(layoutInflater)
            phoneVerificationDialog.setContentView(phoneVerificationDialogBinding.root)
            phoneVerificationDialog.setCancelable(false)
            phoneVerificationDialog.show()
        }
        phoneVerificationDialogBinding.apply {
            code1.setFocus(requireActivity())
            code1.addCodeTextWatcher(requireActivity(), null, code2)
            code2.addCodeTextWatcher(requireActivity(), code1, code3)
            code3.addCodeTextWatcher(requireActivity(), code2, code4)
            code4.addCodeTextWatcher(requireActivity(), code3, null)

            cancelBtn.setOnClickListener {
                val inputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    phoneVerificationDialog.window?.decorView?.windowToken,
                    0
                )
                phoneVerificationDialog.dismiss()
            }

            submitBtn.setOnClickListener {
                if (loginViewModel.isCodeValid(requireContext(), code1, code2, code3, code4)) {
                    val inputMethodManager =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(
                        phoneVerificationDialog.window?.decorView?.windowToken,
                        0
                    )
                    lifecycleScope.launch {
                        loginViewModel.verifyPhone(
                            VerifyPhoneRequest(
                                code = "${code1.text}${code2.text}${code3.text}${code4.text}",
                                phoneNumber = binding.phoneNumberEt.editText?.text.toString().trim()
                                    .filter { !it.isWhitespace() }.substring(1)
                            )
                        ).observe(viewLifecycleOwner) {
                            when (it) {
                                is AuthApiStatus.Error -> {
                                    loginLoading.dismiss()
                                    phoneVerificationDialog.show()
                                    Toast.makeText(
                                        requireContext(),
                                        resources.getString(R.string.verification_code_incorrect),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    code4.setFocus(requireActivity())
                                }

                                is AuthApiStatus.Loading -> {
                                    loginLoading.show()
                                }

                                is AuthApiStatus.Success -> {
                                    loginLoading.dismiss()
                                    login()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loginLoading() {
        try {
            loginLoading.show()
        } catch (e: Exception) {
            loginLoading = AlertDialog.Builder(requireContext()).create()
            loginLoadingDialogBinding = LoadingDialogItemBinding.inflate(layoutInflater)
            loginLoading.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            loginLoading.setCancelable(false)
            loginLoading.setView(loginLoadingDialogBinding.root)
            loginLoading.show()
        }
    }

    private fun loginSuccess() {
        loginLoading.dismiss()
        loginViewModel.setPhoneNumber("")
        loginViewModel.setPassword("")
        findNavController().navigate(
            resId = R.id.action_loginFragment_to_startFragment,
            args = null
        )
    }

    private fun chooseLanguage() {
        loginViewModel.chooseLanguage(
            requireContext(),
            layoutInflater,
            object : LanguageSelectionListener {
                override fun onLanguageSelected(languageCode: String?, languageName: String?) {
                    binding.languageTv.text = languageName
                    updateTexts()
                }

                override fun onCanceled() {

                }

            }
        )
    }

    private fun updateTexts() {
        binding.apply {
            greetingTv.text = resources.getString(R.string.welcome)
            underGreetingTv.text = resources.getString(R.string.lorem_ipsum)
            phoneNumberEt.hint = resources.getString(R.string.phone_number)
            passwordEt.hint = resources.getString(R.string.password)
            loginBtn.text = resources.getString(R.string.login)
            doNotHaveAccountTv.text = resources.getString(R.string.do_not_have_account)
            signUpTv.text = resources.getString(R.string.create_account)
            forgotPasswordTv.text = resources.getString(R.string.forgot_password)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            loginViewModel.phoneNumber.observe(viewLifecycleOwner) {
                phoneNumberEt.editText?.setText(it)
            }
            loginViewModel.password.observe(viewLifecycleOwner) {
                passwordEt.editText?.setText(it)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.apply {
            loginViewModel.setPhoneNumber(phoneNumber = phoneNumberEt.editText?.text.toString())
            loginViewModel.setPassword(password = passwordEt.editText?.text.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}