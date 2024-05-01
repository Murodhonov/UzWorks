package dev.goblingroup.uzworks.fragments.auth

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentSignUpBinding
import dev.goblingroup.uzworks.databinding.LoadingDialogItemBinding
import dev.goblingroup.uzworks.databinding.PhoneVerificationDialogBinding
import dev.goblingroup.uzworks.databinding.ServerErrorDialogBinding
import dev.goblingroup.uzworks.databinding.UserExistsDialogBinding
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.request.SignUpRequest
import dev.goblingroup.uzworks.models.request.VerifyPhoneRequest
import dev.goblingroup.uzworks.models.response.ErrorResponse
import dev.goblingroup.uzworks.utils.SignUpError
import dev.goblingroup.uzworks.utils.addCodeTextWatcher
import dev.goblingroup.uzworks.utils.setFocus
import dev.goblingroup.uzworks.utils.splitFullName
import dev.goblingroup.uzworks.vm.AuthApiStatus
import dev.goblingroup.uzworks.vm.LoginViewModel
import dev.goblingroup.uzworks.vm.SharedSignUpViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {

//    private val TAG = "SignUpFragment"

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var userRole: String

    private val sharedSignUpViewModel: SharedSignUpViewModel by activityViewModels()

    private lateinit var loadingDialog: AlertDialog
    private lateinit var loadingDialogItemBinding: LoadingDialogItemBinding

    private lateinit var serverErrorDialog: AlertDialog
    private lateinit var serverErrorDialogBinding: ServerErrorDialogBinding

    private lateinit var userExistsDialog: AlertDialog
    private lateinit var userExistsDialogBinding: UserExistsDialogBinding

    private lateinit var phoneVerificationDialog: BottomSheetDialog
    private lateinit var phoneVerificationDialogBinding: PhoneVerificationDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            userRole = arguments?.getString("user role")!!
            sharedSignUpViewModel.controlInput(
                fullNameEt,
                phoneNumberEt,
                passwordEt,
                confirmPasswordEt,
                motionLayout
            )

            continueBtn.setOnClickListener {
                if (sharedSignUpViewModel.isFormValid(
                        fullNameEt,
                        phoneNumberEt,
                        passwordEt,
                        confirmPasswordEt,
                        resources
                    )
                ) {
                    signUp()
                }
            }

            signInTv.setOnClickListener {
                findNavController().popBackStack(
                    R.id.loginFragment,
                    false
                )
            }

            back.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun signUp() {
        binding.apply {
            lifecycleScope.launch {
                val (firstName, lastName) = fullNameEt.splitFullName()
                sharedSignUpViewModel.signup(
                    signupRequest = SignUpRequest(
                        confirmPassword = confirmPasswordEt.editText?.text.toString(),
                        firstName = firstName.toString(),
                        lastName = lastName.toString(),
                        password = passwordEt.editText?.text.toString(),
                        phoneNumber = phoneNumberEt.editText?.text.toString().trim()
                            .filter { !it.isWhitespace() }.substring(1),
                        role = userRole
                    )
                ).observe(viewLifecycleOwner) {
                    when (it) {
                        is AuthApiStatus.Error -> {
                            signupError(it.errorResponse)
                        }

                        is AuthApiStatus.Loading -> {
                            signupLoading()
                        }

                        is AuthApiStatus.Success -> {
                            verifyPhone()
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun signupError(errorResponse: ErrorResponse) {
        loadingDialog.dismiss()
        binding.apply {
            when (errorResponse.message) {
                SignUpError.SERVER_ERROR.errorMessage -> {
                    serverError()
                }

                SignUpError.USER_EXISTS_LOGIN.errorMessage -> {
                    userExists()
                }

                else -> {

                }
            }
        }
    }

    private fun serverError() {
        try {
            serverErrorDialog.show()
        } catch (e: java.lang.Exception) {
            serverErrorDialog = AlertDialog.Builder(requireContext()).create()
            serverErrorDialogBinding = ServerErrorDialogBinding.inflate(layoutInflater)
            serverErrorDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            serverErrorDialog.setView(serverErrorDialogBinding.root)
            serverErrorDialog.show()
        }
        serverErrorDialogBinding.close.setOnClickListener {
            serverErrorDialog.dismiss()
        }
    }

    private fun userExists() {
        try {
            userExistsDialog.show()
        } catch (e: java.lang.Exception) {
            userExistsDialog = AlertDialog.Builder(requireContext()).create()
            userExistsDialogBinding = UserExistsDialogBinding.inflate(layoutInflater)
            userExistsDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            userExistsDialog.setView(userExistsDialogBinding.root)
            userExistsDialog.setCancelable(false)
            userExistsDialog.show()
        }
        userExistsDialogBinding.apply {
            loginBtn.setOnClickListener {
                findNavController().popBackStack(
                    R.id.loginFragment,
                    false
                )
            }

            signUpBtn.setOnClickListener {
                userExistsDialog.dismiss()
            }
        }
    }

    private fun signupLoading() {
        try {
            loadingDialog.show()
        } catch (e: java.lang.Exception) {
            loadingDialog = AlertDialog.Builder(requireContext()).create()
            loadingDialogItemBinding = LoadingDialogItemBinding.inflate(layoutInflater)
            loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            loadingDialog.setCancelable(false)
            loadingDialog.setView(loadingDialogItemBinding.root)
            loadingDialog.show()
        }
    }

    private fun verifyPhone() {
        loadingDialog.dismiss()
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
                if (sharedSignUpViewModel.isCodeValid(
                        requireContext(),
                        resources,
                        code1,
                        code2,
                        code3,
                        code4
                    )
                ) {
                    val inputMethodManager =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(
                        phoneVerificationDialog.window?.decorView?.windowToken,
                        0
                    )
                    phoneVerificationDialog.dismiss()

                    lifecycleScope.launch {
                        sharedSignUpViewModel.verifyPhone(
                            VerifyPhoneRequest(
                                code = "${code1.text}${code2.text}${code3.text}${code4.text}",
                                phoneNumber = binding.phoneNumberEt.editText?.text.toString().trim()
                                    .filter { !it.isWhitespace() }.substring(1)
                            )
                        ).observe(viewLifecycleOwner) {
                            when (it) {
                                is AuthApiStatus.Error -> {
                                    Toast.makeText(
                                        requireContext(),
                                        resources.getString(R.string.verification_code_incorrect),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    loadingDialog.dismiss()
                                    phoneVerificationDialog.show()
                                }

                                is AuthApiStatus.Loading -> {
                                    phoneVerificationDialog.dismiss()
                                    loadingDialog.show()
                                }

                                is AuthApiStatus.Success -> {
                                    signupSuccess()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun signupSuccess() {
        binding.apply {
            lifecycleScope.launch {
                loginViewModel.login(
                    loginRequest = LoginRequest(
                        password = passwordEt.editText?.text.toString(),
                        phoneNumber = phoneNumberEt.editText?.text.toString().trim().filter { !it.isWhitespace() }.substring(1)
                    )
                ).observe(viewLifecycleOwner) {
                    when (it) {
                        is AuthApiStatus.Error -> {
                            loadingDialog.dismiss()
                            Toast.makeText(
                                requireContext(),
                                "error on logging in",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is AuthApiStatus.Loading -> {

                        }

                        is AuthApiStatus.Success -> {
                            sharedSignUpViewModel.setFullName("")
                            sharedSignUpViewModel.setPhoneNumber("")
                            sharedSignUpViewModel.setPassword("")
                            sharedSignUpViewModel.setConfirmPassword("")
                            loadingDialog.dismiss()
                            findNavController().navigate(
                                resId = R.id.action_signUpFragment_to_succeedFragment,
                                args = null
                            )
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            sharedSignUpViewModel.fullName.observe(viewLifecycleOwner) {
                fullNameEt.editText?.setText(it)
            }
            sharedSignUpViewModel.phoneNumber.observe(viewLifecycleOwner) {
                phoneNumberEt.editText?.setText(it)
            }
            sharedSignUpViewModel.password.observe(viewLifecycleOwner) {
                passwordEt.editText?.setText(it)
            }
            sharedSignUpViewModel.confirmPassword.observe(viewLifecycleOwner) {
                confirmPasswordEt.editText?.setText(it)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.apply {
            sharedSignUpViewModel.setFullName(fullNameEt.editText?.text.toString())
            sharedSignUpViewModel.setPhoneNumber(phoneNumberEt.editText?.text.toString())
            sharedSignUpViewModel.setPassword(passwordEt.editText?.text.toString())
            sharedSignUpViewModel.setConfirmPassword(confirmPasswordEt.editText?.text.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}