package dev.goblingroup.uzworks.fragments.auth

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.AuthDialogItemBinding
import dev.goblingroup.uzworks.databinding.FragmentSignUpBinding
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.request.SignUpRequest
import dev.goblingroup.uzworks.utils.getNavOptions
import dev.goblingroup.uzworks.utils.isStrongPassword
import dev.goblingroup.uzworks.utils.splitFullName
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.LoginViewModel
import dev.goblingroup.uzworks.vm.SignUpViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val TAG = "SignUpFragment"

    private val signupViewModel: SignUpViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var authDialog: AlertDialog
    private lateinit var authDialogBinding: AuthDialogItemBinding

    private lateinit var userRole: String

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
            fullNameEt.editText?.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    motionLayout.setTransitionDuration(500)
                    motionLayout.transitionToEnd()
                    motionLayout.setTransitionDuration(1000)
                }
            }
            usernameEt.editText?.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    motionLayout.setTransitionDuration(500)
                    motionLayout.transitionToEnd()
                    motionLayout.setTransitionDuration(1000)
                }
            }
            passwordEt.editText?.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    motionLayout.setTransitionDuration(500)
                    motionLayout.transitionToEnd()
                    motionLayout.setTransitionDuration(1000)
                }
            }
            confirmPasswordEt.editText?.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    motionLayout.setTransitionDuration(500)
                    motionLayout.transitionToEnd()
                    motionLayout.setTransitionDuration(1000)
                }
            }
            continueBtn.setOnClickListener {
                if (isFormValid()) {
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

            fullNameEt.editText?.addTextChangedListener {
                if (fullNameEt.isErrorEnabled && it.toString().isNotEmpty()) {
                    fullNameEt.isErrorEnabled = false
                }
            }

            usernameEt.editText?.addTextChangedListener {
                if (usernameEt.isErrorEnabled && it.toString().isNotEmpty()) {
                    usernameEt.isErrorEnabled = false
                }
            }

            passwordEt.editText?.addTextChangedListener {
                if (passwordEt.isErrorEnabled && it.toString().isNotEmpty()) {
                    passwordEt.isErrorEnabled = false
                }
            }

            confirmPasswordEt.editText?.addTextChangedListener {
                if (confirmPasswordEt.isErrorEnabled && it.toString()
                        .isNotEmpty()
                ) {
                    confirmPasswordEt.isErrorEnabled = false
                }
            }

            fullNameEt.onFocusChangeListener =
                OnFocusChangeListener { view, hasFocus ->
                    if (hasFocus) {
                        motionLayout.setTransitionDuration(500)
                        motionLayout.transitionToEnd()
                        motionLayout.setTransitionDuration(1000)
                    }
                }
        }
    }

    private fun isFormValid(): Boolean {
        binding.apply {
            var validation = true
            val (firstName, lastName) = fullNameEt.splitFullName()
            if (firstName == null && lastName == null) {
                fullNameEt.isErrorEnabled = true
                fullNameEt.error = resources.getString(R.string.enter_full_name)
                validation = false
            }
            if (usernameEt.editText?.text.toString().isEmpty()) {
                usernameEt.isErrorEnabled = true
                usernameEt.error = resources.getString(R.string.enter_username)
                validation = false
            }
            if (passwordEt.editText?.text.toString().isEmpty()) {
                passwordEt.isErrorEnabled = true
                passwordEt.error = resources.getString(R.string.enter_password)
                validation = false
            } else {
                if (!passwordEt.editText?.text.toString().isStrongPassword()) {
                    passwordEt.isErrorEnabled = true
                    passwordEt.error = resources.getString(R.string.password_requirements)
                    validation = false
                }
            }
            if (confirmPasswordEt.editText?.text.toString().isEmpty() ||
                confirmPasswordEt.editText?.text.toString() != passwordEt.editText?.text.toString()
            ) {
                confirmPasswordEt.isErrorEnabled = true
                confirmPasswordEt.error = resources.getString(R.string.confirm_password)
                validation = false
            }
            return validation
        }
    }

    private fun signUp() {
        binding.apply {
            lifecycleScope.launch {
                val (firstName, lastName) = fullNameEt.splitFullName()
                signupViewModel.signup(
                    signupRequest = SignUpRequest(
                        username = usernameEt.editText?.text.toString(),
                        password = passwordEt.editText?.text.toString(),
                        confirmPassword = confirmPasswordEt.editText?.text.toString(),
                        firstName = firstName.toString(),
                        lastName = lastName.toString(),
                        role = userRole
                    )
                ).observe(viewLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {
                            signupError(it.error)
                        }

                        is ApiStatus.Loading -> {
                            signupLoading()
                        }

                        is ApiStatus.Success -> {
                            signupSuccess()
                        }
                    }
                }
            }
        }
    }

    private fun signupError(signUpError: Throwable) {
        authDialogBinding.apply {
            Log.e(TAG, "signupError: $signUpError")
            Log.e(TAG, "signupError: ${signUpError.stackTrace}")
            Log.e(TAG, "signupError: ${signUpError.printStackTrace()}")
            errorIv.visibility = View.VISIBLE
            dialogTv.text = "Error ${signUpError.message.toString()}"
            closeDialog.visibility = View.VISIBLE
            closeDialog.setOnClickListener {
                authDialog.dismiss()
            }
        }
    }

    private fun signupLoading() {
        authDialog = AlertDialog.Builder(requireContext()).create()
        authDialogBinding = AuthDialogItemBinding.inflate(layoutInflater)
        authDialog.setView(authDialogBinding.root)
        authDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        authDialog.setCancelable(false)
        authDialog.show()
    }

    private fun signupSuccess() {
        binding.apply {
            lifecycleScope.launch {
                loginViewModel.login(
                    loginRequest = LoginRequest(
                        usernameEt.editText?.text.toString(),
                        passwordEt.editText?.text.toString()
                    )
                ).observe(viewLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {

                        }

                        is ApiStatus.Loading -> {

                        }

                        is ApiStatus.Success -> {
                            authDialog.dismiss()
                            findNavController().navigate(
                                resId = R.id.succeedFragment,
                                args = null,
                                navOptions = getNavOptions()
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}