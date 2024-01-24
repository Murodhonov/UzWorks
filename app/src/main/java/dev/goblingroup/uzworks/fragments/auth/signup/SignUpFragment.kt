package dev.goblingroup.uzworks.fragments.auth.signup

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.database.AppDatabase
import dev.goblingroup.uzworks.databinding.AuthDialogItemBinding
import dev.goblingroup.uzworks.databinding.FragmentSignUpBinding
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.request.SignUpRequest
import dev.goblingroup.uzworks.models.response.SignUpResponse
import dev.goblingroup.uzworks.networking.ApiClient
import dev.goblingroup.uzworks.utils.ApiStatus
import dev.goblingroup.uzworks.utils.NetworkHelper
import dev.goblingroup.uzworks.utils.getNavOptions
import dev.goblingroup.uzworks.utils.splitFullName
import dev.goblingroup.uzworks.vm.LoginViewModel
import dev.goblingroup.uzworks.vm.LoginViewModelFactory
import dev.goblingroup.uzworks.vm.SignUpViewModel
import dev.goblingroup.uzworks.vm.SignUpViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SignUpFragment : Fragment(), CoroutineScope {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val TAG = "SignUpFragment"

    private lateinit var signupViewModel: SignUpViewModel
    private lateinit var signupViewModelFactory: SignUpViewModelFactory
    private lateinit var networkHelper: NetworkHelper

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

            networkHelper = NetworkHelper(requireContext())
            signupViewModelFactory = SignUpViewModelFactory(
                authService = ApiClient.authService,
                networkHelper = networkHelper
            )

            continueBtn.setOnClickListener {
                if (
                    fullNameEt.editText?.text.toString().isNotEmpty() &&
                    usernameEt.editText?.text.toString().isNotEmpty() &&
                    passwordEt.editText?.text.toString().isNotEmpty() &&
                    confirmPasswordEt.editText?.text.toString().isNotEmpty()
                ) {
                    /**
                     * check if password and confirm passwords are equal
                     * check full name format
                     */
                    if (confirmPasswordEt.editText?.text.toString() != passwordEt.editText?.text.toString()) {
                        confirmPasswordEt.error = "Please confirm the password"
                        confirmPasswordEt.isErrorEnabled = true
                    } else {
                        val (firstName, lastName) = splitFullName(fullName = fullNameEt.editText?.text.toString())
                        if (firstName.isEmpty() || lastName.isEmpty()) {
                            fullNameEt.error = "Please enter your firstname and lastname"
                            fullNameEt.isErrorEnabled = true
                        } else {
                            signUp(firstName, lastName)
                        }
                    }
                } else {
                    if (fullNameEt.editText?.text.toString().isEmpty()) {
                        fullNameEt.isErrorEnabled = true
                        fullNameEt.error = "Enter full name"
                    }
                    if (usernameEt.editText?.text.toString().isEmpty()) {
                        usernameEt.isErrorEnabled = true
                        usernameEt.error = "Enter username"
                    }
                    if (passwordEt.editText?.text.toString().isEmpty()) {
                        passwordEt.isErrorEnabled = true
                        passwordEt.error = "Enter password"
                    }
                    if (confirmPasswordEt.editText?.text.toString().isEmpty()) {
                        confirmPasswordEt.isErrorEnabled = true
                        confirmPasswordEt.error = "Confirm password"
                    }
                }
            }

            signUpWithGoogleBtn.setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    "In this stage should be google sign in",
                    Toast.LENGTH_SHORT
                ).show()
            }

            signInTv.setOnClickListener {
                findNavController().popBackStack()
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
                    confirmPasswordEt.error = "Confirm password"
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

    private fun signUp(firstName: String, lastName: String) {
        binding.apply {
            signupViewModel = ViewModelProvider(
                owner = this@SignUpFragment,
                factory = signupViewModelFactory
            )[SignUpViewModel::class.java]

            launch {
                signupViewModel.signup(
                    signupRequest = SignUpRequest(
                        username = usernameEt.editText?.text.toString(),
                        password = passwordEt.editText?.text.toString(),
                        confirmPassword = confirmPasswordEt.editText?.text.toString(),
                        firstName = firstName,
                        lastName = lastName,
                        role = userRole
                    )
                )
                    .collect {
                        when (it) {
                            is ApiStatus.Error -> {
                                signupError(it.error)
                            }

                            is ApiStatus.Loading -> {
                                signupLoading()
                            }

                            is ApiStatus.Success -> {
                                signupSuccess(it.response as SignUpResponse)
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
            dialogTv.text = "some error while signing up"
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
        authDialog.setCancelable(false)
        authDialog.show()
    }

    private fun signupSuccess(signupResponse: SignUpResponse) {
        binding.apply {
            val loginViewModel = ViewModelProvider(
                owner = this@SignUpFragment,
                LoginViewModelFactory(
                    appDatabase = AppDatabase.getInstance(requireContext()),
                    authService = ApiClient.authService,
                    networkHelper,
                    context = requireContext()
                )
            )[LoginViewModel::class.java]
            launch {
                loginViewModel.login(
                    loginRequest = LoginRequest(
                        usernameEt.editText?.text.toString(),
                        passwordEt.editText?.text.toString()
                    )
                )
                    .collect {
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

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}