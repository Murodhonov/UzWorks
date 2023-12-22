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
import dev.goblingroup.uzworks.databinding.AuthDialogItemBinding
import dev.goblingroup.uzworks.databinding.FragmentSignUpBinding
import dev.goblingroup.uzworks.models.request.SignUpRequest
import dev.goblingroup.uzworks.models.response.SignUpResponse
import dev.goblingroup.uzworks.networking.ApiClient
import dev.goblingroup.uzworks.utils.NetworkHelper
import dev.goblingroup.uzworks.resource.SignUpResource
import dev.goblingroup.uzworks.utils.extensions.showHidePassword
import dev.goblingroup.uzworks.utils.getNavOptions
import dev.goblingroup.uzworks.utils.splitFullName
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
                networkHelper = networkHelper,
                signupRequest = SignUpRequest(
                    username = usernameEt.text.toString(),
                    password = passwordEt.text.toString(),
                    confirmPassword = confirmPasswordEt.text.toString(),
                    firstName = "",
                    lastName = "",
                    role = ""
                )
            )

            continueBtn.setOnClickListener {
                if (
                    fullNameEt.text.toString().isNotEmpty() &&
                    usernameEt.text.toString().isNotEmpty() &&
                    passwordEt.text.toString().isNotEmpty() &&
                    confirmPasswordEt.text.toString().isNotEmpty()
                ) {
                    /**
                     * check if password and confirm passwords are equal
                     * check full name format
                     */
                    if (confirmPasswordEt.text.toString() != passwordEt.text.toString()) {
                        confirmPasswordErrorTv.text = "Please confirm the password"
                        confirmPasswordErrorLayout.visibility = View.VISIBLE
                        confirmPasswordEt.setBackgroundResource(R.drawable.error_edit_text_background)
                    } else {
                        val (firstName, lastName) = splitFullName(fullName = fullNameEt.text.toString())
                        if (firstName.isEmpty() || lastName.isEmpty()) {
                            fullnameErrorTv.text = "Please enter your firstname and lastname"
                            fullNameErrorLayout.visibility = View.VISIBLE
                            fullNameEt.setBackgroundResource(R.drawable.error_edit_text_background)
                        } else {
                            signUp(firstName, lastName)
                        }
                    }
                } else {
                    if (fullNameEt.text.toString().isEmpty()) {
                        fullNameErrorLayout.visibility = View.VISIBLE
                        fullNameEt.setBackgroundResource(R.drawable.error_edit_text_background)
                    }
                    if (usernameEt.text.toString().isEmpty()) {
                        usernameErrorLayout.visibility = View.VISIBLE
                        usernameEt.setBackgroundResource(R.drawable.error_edit_text_background)
                    }
                    if (passwordEt.text.toString().isEmpty()) {
                        passwordErrorLayout.visibility = View.VISIBLE
                        passwordEt.setBackgroundResource(R.drawable.error_edit_text_background)
                    }
                    if (confirmPasswordEt.text.toString().isEmpty()) {
                        confirmPasswordErrorLayout.visibility = View.VISIBLE
                        confirmPasswordEt.setBackgroundResource(R.drawable.error_edit_text_background)
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

            fullNameEt.addTextChangedListener {
                if (fullNameErrorLayout.visibility == View.VISIBLE && it.toString().isNotEmpty()) {
                    fullNameErrorLayout.visibility = View.GONE
                    fullNameEt.setBackgroundResource(R.drawable.registration_edit_text_background)
                }
            }

            usernameEt.addTextChangedListener {
                if (usernameErrorLayout.visibility == View.VISIBLE && it.toString().isNotEmpty()) {
                    usernameErrorLayout.visibility = View.GONE
                    usernameEt.setBackgroundResource(R.drawable.registration_edit_text_background)
                }
            }

            passwordEt.addTextChangedListener {
                if (passwordErrorLayout.visibility == View.VISIBLE && it.toString().isNotEmpty()) {
                    passwordErrorLayout.visibility = View.GONE
                    passwordEt.setBackgroundResource(R.drawable.registration_edit_text_background)
                }
            }

            confirmPasswordEt.addTextChangedListener {
                if (confirmPasswordErrorLayout.visibility == View.VISIBLE && it.toString()
                        .isNotEmpty()
                ) {
                    confirmPasswordErrorLayout.visibility = View.GONE
                    confirmPasswordEt.setBackgroundResource(R.drawable.registration_edit_text_background)
                    confirmPasswordErrorTv.text = "Confirm password should enter"
                }
            }

            passwordEyeIv.setOnClickListener {
                passwordEt.showHidePassword(requireContext(), passwordEyeIv)
            }

            confirmPasswordEyeIv.setOnClickListener {
                confirmPasswordEt.showHidePassword(requireContext(), confirmPasswordEyeIv)
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
            signupViewModelFactory.signupRequest.username = usernameEt.text.toString()
            signupViewModelFactory.signupRequest.password = passwordEt.text.toString()
            signupViewModelFactory.signupRequest.confirmPassword = confirmPasswordEt.text.toString()
            signupViewModelFactory.signupRequest.firstName = firstName
            signupViewModelFactory.signupRequest.lastName = lastName
            signupViewModelFactory.signupRequest.role = userRole
            signupViewModel = ViewModelProvider(
                owner = this@SignUpFragment,
                factory = signupViewModelFactory
            )[SignUpViewModel::class.java]

            launch {
                signupViewModel.signup()
                    .collect {
                        when (it) {
                            is SignUpResource.SignUpError -> {
                                signupError(it.signupError)
                            }

                            is SignUpResource.SignUpLoading -> {
                                signupLoading()
                            }

                            is SignUpResource.SignUpSuccess -> {
                                signupSuccess(it.signupResponse)
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
        Log.d(TAG, "signupSuccess: for ${signupViewModelFactory.signupRequest} $signupResponse")
        authDialog.dismiss()
        findNavController().navigate(
            resId = R.id.succeedFragment,
            args = null,
            navOptions = getNavOptions()
        )
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}